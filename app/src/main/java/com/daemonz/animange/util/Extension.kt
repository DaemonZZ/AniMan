package com.daemonz.animange.util

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.daemonz.animange.R

@BindingAdapter("set_image")
fun AppCompatImageView.setImageFromUrl(url: String?) {
    url?.let {
        Glide.with(this).load(it)
            .error(R.drawable.app_logo_gray)
            .placeholder(R.drawable.app_logo_gray)
            .into(this)
    }
}
fun AppCompatImageView.loadGif(res: Int) {
    Glide.with(this).load(res).into(this)
}
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.pxToDp(px: Int): Int {
    return (px / resources.displayMetrics.density).toInt()
}