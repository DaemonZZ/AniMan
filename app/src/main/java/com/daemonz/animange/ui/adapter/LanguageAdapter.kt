package com.daemonz.animange.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemLanguageBinding
import com.daemonz.animange.entity.Language
import com.daemonz.animange.log.ALog
import java.util.Locale

class LanguageAdapter(onItemClickListener: OnItemClickListener<Language>) :
    BaseRecyclerAdapter<Language, ItemLanguageBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemLanguageBinding
        get() = ItemLanguageBinding::inflate

    override fun bindView(binding: ItemLanguageBinding, item: Language, position: Int) {
        binding.apply {
            val currentLanguage = Locale.getDefault().language
            ALog.d(TAG, "language: $currentLanguage")
            checkbox.isVisible = currentLanguage == item.id
            title.text = getLanguageName(item, root.context)
            icon.setImageResource(getLangIcons(item))
        }
    }

    private fun getLanguageName(item: Language, context: Context): String {
        return when (item.id) {
            "en" -> context.getString(R.string.english)
            "vi" -> context.getString(R.string.vietnamese)
            else -> "Unknown"
        }
    }

    private fun getLangIcons(item: Language) = when (item.id) {
        "en" -> R.drawable.ic_lang_us
        "vi" -> R.drawable.ic_lang_vi
        else -> R.drawable.ic_profile
    }

}