package com.daemonz.animange.viewmodel

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Account
import com.daemonz.animange.entity.Activity
import com.daemonz.animange.entity.UpdateData
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserAction
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.AppThemeManager
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.POLICY_URL
import com.daemonz.animange.util.SharePreferenceManager
import com.daemonz.animange.util.TERMS_URL
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    @Inject
    lateinit var sharePreferenceManager: SharePreferenceManager
    private var signInLauncher: ActivityResultLauncher<Intent>? = null
    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error
    private val _account = MutableLiveData<Account?>()
    val account: LiveData<Account?> = _account

    private val _hasNewUpdate = MutableLiveData<UpdateData?>(null)
    val hasNewUpdate: LiveData<UpdateData?> = _hasNewUpdate

    fun registerSigningLauncher(activity: AppCompatActivity) {
        ALog.d(TAG, "registerSigningLauncher: ")
        signInLauncher = activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
            ALog.d(TAG, "registerSigningLauncher: $res")
            this.onSignInResult(res)
        }
        FirebaseAuth.getInstance().currentUser?.let {
            ALog.d(TAG, "registerSigningLauncher: $it")
            checkAccount(it)
        }
    }

    fun createSigningLauncher() {
        // Choose authentication providers
        ALog.d(TAG, "createSigningLauncher")
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.login_layout)
            .setEmailButtonId(R.id.email_login)
            .setTosAndPrivacyPolicyId(R.id.text_tos)
            .setGoogleButtonId(R.id.google_login)
            .build()

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(AppThemeManager.getTheme(sharePreferenceManager))
            .setTosAndPrivacyPolicyUrls(TERMS_URL, POLICY_URL)
            .setAuthMethodPickerLayout(customLayout)
            .build()
        signInLauncher?.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        ALog.d(TAG, "onSignInResult: $response")
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            checkAccount(user)
            LoginData.currentError = null
            _error.value = null
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            LoginData.currentError = response?.error
            ALog.e(TAG, "onSignInResult: ${result.resultCode}")
            LoginData.account = null
            _error.value = response?.error
            _account.value = null
        }
    }

    private fun checkAccount(user: FirebaseUser?) {
        if (user == null) {
            return
        }
        repository.getAccount(user.uid).addOnSuccessListener { acc ->
            if (acc.toObject(Account::class.java) != null) {
                val account = acc.toObject(Account::class.java)
                LoginData.account = account
                _account.value = account
                val activity = Activity(
                    id = UUID.randomUUID().toString(),
                    activity = UserAction.Login,
                    content = "${LoginData.account?.name} 's just logged in"
                )
                syncActivity(activity)
                if (account != null && account.region == null) {
                    account.region = Locale.getDefault().country
                }
                account?.lastLogin = Date.from(Instant.now())
                if (account != null) {
                    repository.saveAccount(account)
                }
            } else {
                val newAccount = Account(
                    id = user.uid,
                    email = user.email,
                    name = user.displayName,
                    region = Locale.getDefault().country,
                    lastLogin = Date.from(Instant.now()),
                    users = listOf(
                        User(
                            id = UUID.randomUUID().toString(),
                            name = user.displayName,
                            userType = UserType.ADULT,
                            image = 1,
                            isMainUser = true,
                            isActive = true,
                            createdAt = Date.from(Instant.now())
                        )
                    ),
                )
                repository.saveAccount(newAccount)
                _account.value = newAccount
                // LoginData.account is assigned in repository.saveAccount
                val activity = Activity(
                    id = UUID.randomUUID().toString(),
                    activity = UserAction.Register,
                    content = "${newAccount.name} 's just register account"
                )
                syncActivity(activity)
            }
        }.addOnFailureListener { e ->
            _error.value = e
        }
    }

    fun logout(context: Context) {
        ALog.d(TAG, "logout: ")
        AuthUI.getInstance().signOut(context).addOnSuccessListener {
            val activity = Activity(
                id = UUID.randomUUID().toString(),
                activity = UserAction.Logout,
                content = "${LoginData.account?.name} 's just logged out"
            )
            syncActivity(activity)
            LoginData.account = null
            _account.value = null
            _error.value = null
        }.addOnFailureListener {
            _error.value = it
        }
    }

    fun isLoggedIn(): Boolean {
        val res = FirebaseAuth.getInstance().currentUser != null
        if (res && LoginData.account == null) {
            checkAccount(FirebaseAuth.getInstance().currentUser)
        }
        return res
    }

    fun checkForUpdate() {
        ALog.d(TAG, "checkForUpdate: ")
        repository.getUpdateData().addOnSuccessListener {
            it.toObject(UpdateData::class.java)?.let { updateData ->
                if (updateData.version != BuildConfig.VERSION_NAME) {
                    ALog.d(TAG, "checkForUpdate: $updateData")
                    _hasNewUpdate.value = updateData
                }
            }
        }.addOnFailureListener {
            ALog.e(TAG, "checkForUpdate: $it")
            _error.value = it
        }
    }
    fun syncActivity(activity: Activity) {
//        repository.syncActivity(activity)
    }
}