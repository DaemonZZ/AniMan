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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.daemonz.animange.R
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog

private const val TAG = "Extension"

@BindingAdapter("set_image")
fun AppCompatImageView.setImageFromUrl(
    url: String?,
    placeHolder: Int? = null,
    error: Int? = null,
    cornerRadius: Int = 1
) {
    url?.let {
        Glide.with(this).load(it)
            .error(error ?: R.drawable.app_logo_gray)
            .placeholder(placeHolder ?: R.drawable.loading_288px)
            .transform(RoundedCorners(cornerRadius))
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
    originName = originName
)

fun Int?.toImageResource(): Int {
    return when (this) {
        1 -> R.drawable.avt_1
        2 -> R.drawable.avt_2
        3 -> R.drawable.avt_3
        4 -> R.drawable.avt_4
        5 -> R.drawable.avt_5
        6 -> R.drawable.avt_6
        7 -> R.drawable.avt_7
        8 -> R.drawable.avt_8
        9 -> R.drawable.avt_9
        10 -> R.drawable.avt_10
        11 -> R.drawable.avt_11
        12 -> R.drawable.avt_12
        13 -> R.drawable.avt_13
        14 -> R.drawable.avt_14
        15 -> R.drawable.avt_15
        16 -> R.drawable.avt_16
        17 -> R.drawable.avt_17
        18 -> R.drawable.avt_18
        19 -> R.drawable.avt_19
        20 -> R.drawable.avt_20
        else -> R.drawable.avt_1
    }
}
fun String.makeSearchText(): String {
    var text = this
    if (text.contains("  ")) {
        text = text.replace("  ", " ")
    }
    return text
}