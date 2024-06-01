package com.daemonz.animange

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentManager
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.dialog.LoadingOverLay
import com.daemonz.animange.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    val viewModel: MainViewModel by viewModels()
    private val loadingRequest = mutableSetOf<String>()
    private val loadingDialog: LoadingOverLay by lazy {
        LoadingOverLay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    fun showLoadingOverlay(fm: FragmentManager, id: String) {
        ALog.d(TAG, "showLoadingOverlay $id dd: ${loadingRequest.size}")
        loadingRequest.add(id)
        if (!loadingDialog.isAdded && loadingRequest.size == 1) {
            ALog.d(TAG, "showLoadingOverlay showed")
            loadingDialog.show(fm, "LoadingOverLay")
        }
    }

    fun hideLoadingOverlay(id: String) {
        ALog.d(TAG, "hideLoadingOverlay $id dd: ${loadingRequest.size}")
        loadingRequest.remove(id)
        if (loadingRequest.isEmpty()) {
            loadingDialog.dismiss()
        }
    }
}