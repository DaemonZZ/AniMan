package com.daemonz.animange.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import retrofit2.Response

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

fun AppCompatImageView.loadImageFromStorage(id: Int) {
    val ref = Firebase.storage.reference.child("avatar/avt_$id.png")
    ref.downloadUrl.addOnSuccessListener { url ->
        Glide.with(this.context)
            .load(url)
            .into(this)
    }
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
    originName = originName,
    episodeCurrent = episodeCurrent
)

fun String.makeSearchText(): String {
    var text = this
    if (text.contains("  ")) {
        text = text.replace("  ", " ")
    }
    return text
}

fun String.isValidName(): Boolean {
    return !(this.length < 3 || this.length > 20)
}

fun Activity.getToolbarHeight(): Int {
    val tv = TypedValue()
    if (this.theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
        return TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    }
    return 0
}

fun <T> Response<T>.addOnCompleteListener(listener: (T) -> Unit): Response<T> {
    if (this.isSuccessful && this.body() != null) {
        this.body()?.let { listener.invoke(it) }
    }
    return this
}

fun <T> Response<T>.addOnFailureListener(listener: (String) -> Unit): Response<T> {
    if (!this.isSuccessful || this.errorBody() != null) {
        this.errorBody()?.let { listener.invoke(it.string()) }
    }
    return this
}