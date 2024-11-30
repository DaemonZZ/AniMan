package com.daemonz.animange.ui.dialog

import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.daemonz.animange.R
import com.daemonz.animange.databinding.TransparentLayoutBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.LightTheme
import com.daemonz.animange.util.dpToPx


class PlayerMaskDialog(private val onClick: () -> Unit) : BaseDialog(LightTheme()) {
    private var _binding: TransparentLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TransparentLayoutBinding.inflate(inflater, container, false)
        dialog?.window?.let {
            val param = it.attributes
            param.width = LayoutParams.WRAP_CONTENT
            param.height = LayoutParams.WRAP_CONTENT
            param.type = LayoutParams.TYPE_APPLICATION_PANEL
            param.flags =
                LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_SPLIT_TOUCH or LayoutParams.FLAG_LAYOUT_NO_LIMITS
            param.format = PixelFormat.TRANSLUCENT
            param.alpha = 0.01f
            param?.gravity = Gravity.END or Gravity.BOTTOM
            param.x = requireContext().dpToPx(40)
            it.attributes = param
            it.clearFlags(LayoutParams.FLAG_DIM_BEHIND)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btn.isClickable = true
            btn.setOnClickListener {
                onClick.invoke()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}