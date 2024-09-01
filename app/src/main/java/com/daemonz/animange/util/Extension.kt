package com.daemonz.animange.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.R
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.google.android.material.textview.MaterialTextView
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
            .error(error ?: R.drawable.film_placeholder)
            .placeholder(placeHolder ?: R.drawable.film_placeholder)
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
        try {
            Glide.with(this.context)
                .load(url)
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder)
                .into(this)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this.context,
                this.context.getString(R.string.error_when_load_image),
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}

fun AppCompatTextView.makeTextLink(
    textLink: String,
    underline: Boolean = false,
    color: Int? = ContextCompat.getColor(context, R.color.md_theme_primary),
    bold: Boolean = false,
    onClick: () -> Unit = {}
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
            ds.isFakeBoldText = bold
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

@SuppressLint("ClickableViewAccessibility")
fun EditText.setupClearButtonWithAction(theme: AnimanTheme, action: () -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val clearIcon = if (editable?.isNotEmpty() == true) theme.iconClose() else 0
            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                if (text.toString().isNotEmpty()) {
                    this.setText(STRING_EMPTY)
                    this.clearFocus()
                    AppUtils.hideKeyboard(context, this)
                    action.invoke()
                }
                return@OnTouchListener true
            }
        }
        return@OnTouchListener false
    })
}

fun Item.isFavorite(): Boolean {
    return LoginData.getActiveUser()?.favorites?.map { it.slug }?.let {
        if (it.contains(slug)) {
            return@let true
        } else {
            return@let false
        }
    } ?: false
}
fun Item.isSensor(filter: Boolean): Boolean {
    if (filter) {
        return this.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
    } else {
        return false
    }
}