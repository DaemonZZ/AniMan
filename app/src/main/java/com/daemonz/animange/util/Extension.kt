package com.daemonz.animange.util

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.daemonz.animange.R
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog

private const val TAG = "Extension"
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

fun AppCompatTextView.makeTextLink(
    textLink: String,
    underline: Boolean = false,
    color: Int? = ContextCompat.getColor(context, R.color.md_theme_primary),
    onClick: () -> Unit
) {
    val spannableString = SpannableString(text)
    val textColor = color ?: currentTextColor
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onClick.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = underline
            ds.color = textColor
        }
    }
    val index = spannableString.indexOf(textLink)
    kotlin.runCatching {
        spannableString.setSpan(
            clickableSpan,
            index,
            index + textLink.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }.getOrElse { ALog.e(TAG, "makeTextLinkFail \n $it") }
    text = spannableString
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.pxToDp(px: Int): Int {
    return (px / resources.displayMetrics.density).toInt()
}
fun Item.toFavouriteItem(img: String): FavouriteItem = FavouriteItem(
    slug = slug,
    name = name,
    category = category.map { it.name },
    imageUrl = img,
)