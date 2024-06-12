package com.daemonz.animange.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.daemonz.animange.R
import com.daemonz.animange.entity.Account
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.repo.DataRepository
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginHelper(
    private val dataRepository: DataRepository,
    private val context: Context,
    private val scope: CoroutineScope,
) {
    companion object {
        private const val TAG = "LoginHelper"
    }

    private var signInLauncher: ActivityResultLauncher<Intent>? = null
    private var auth: FirebaseAuth? = null

    fun registerSigningLauncher(activity: AppCompatActivity) {
        ALog.d(TAG, "registerSigningLauncher: ")
        signInLauncher = activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
            this.onSignInResult(res)
        }
    }

    fun createSigningLauncher() {
        // Choose authentication providers
        ALog.d(TAG, "createSigningLauncher")
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val layout = listOf(
            R.layout.login_layout,
            R.layout.login_layout2,
            R.layout.login_layout3,
        ).random()

        val customLayout = AuthMethodPickerLayout.Builder(layout)
            .setEmailButtonId(R.id.email_login)
            .setPhoneButtonId(R.id.phone_login)
            .setTosAndPrivacyPolicyId(R.id.text_tos)
            .setGoogleButtonId(R.id.google_login)
            .build()

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.AppTheme)
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
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            LoginData.currentError = response?.error
            ALog.e(TAG, "onSignInResult: ${result.resultCode}")
            LoginData.account = null
        }
    }

    private fun checkAccount(user: FirebaseUser?) {
        if (user == null) {
            return
        }
        scope.launch {
            val account = dataRepository.getAccount(user.uid)
            if (account != null) {
//                LoginData.account = account
            } else {
                val newAccount = Account(
                    id = user.uid,
                    email = user.email,
                    name = user.displayName,
                    users = listOf(User(name = user.displayName, userType = UserType.ADULT))
                )
                dataRepository.saveAccount(newAccount)
            }
        }
    }

    fun initFireBaseAuth(context: AppCompatActivity) {
        auth = Firebase.auth
    }

    fun createNewUser(email: String, password: String, activity: AppCompatActivity) {
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    ALog.d(TAG, "createUserWithEmail:success")
                    val user = auth?.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    ALog.w(TAG, "createUserWithEmail:failure ${task.exception}")
                    Toast.makeText(
                        activity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    //updateUI(null)
                }
            }
    }

    fun login(email: String, password: String, activity: AppCompatActivity) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    ALog.d(TAG, "signInWithEmail:success")
                    val user = auth?.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    ALog.w(TAG, "signInWithEmail:failure ${task.exception}")
                    Toast.makeText(
                        activity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
//                    updateUI(null)
                }
            }
    }

    private fun handleFailure(e: GetCredentialException) {
        e.printStackTrace()
    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential
        ALog.d(TAG, "handleSignIn: $credential")
        when (credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
                val responseJson = credential.authenticationResponseJson
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                    } catch (e: GoogleIdTokenParsingException) {
                        ALog.e(TAG, "Received an invalid google id token response")
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    ALog.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                ALog.e(TAG, "Unexpected type of credential")
            }
        }
    }

    fun logout() {
        ALog.d(TAG, "logout: ")
        AuthUI.getInstance().signOut(context)
    }

    fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}