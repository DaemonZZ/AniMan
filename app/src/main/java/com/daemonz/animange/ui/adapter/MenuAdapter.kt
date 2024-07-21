package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.MenuItemBinding
import com.daemonz.animange.entity.MenuItem
import com.daemonz.animange.entity.MenuItemType
import com.daemonz.animange.ui.thememanager.AnimanTheme

class MenuAdapter(
    private val onItemClickListener: OnItemClickListener<MenuItem>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<MenuItem, MenuItemBinding>(onItemClickListener, theme) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MenuItemBinding
        get() = MenuItemBinding::inflate

    override fun bindView(binding: MenuItemBinding, item: MenuItem, position: Int) {
        binding.apply {
            root.setBackgroundColor(theme.menuItemBackground(root.context))
            title.text = item.title
            title.setTextColor(theme.firstActivityTextColor(root.context))
            if (position == itemCount - 1) {
                divider.isVisible = false
            }
            when (item.type) {
                MenuItemType.Theme -> {
                    arrow.isVisible = false
                    arrow.setImageResource(theme.iconNext())
                    icon.setColorFilter(
                        ContextCompat.getColor(
                            root.context,
                            getThemesColor(position)
                        )
                    )
                    root.setOnClickListener {
                        onItemClickListener.onItemClick(item, position)
                    }
                }

                else -> {}
            }
        }
    }

    private fun getThemesColor(theme: Int): Int {
        return when (theme) {
            0 -> R.color.md_theme_primary
            1 -> R.color.md_theme_primary_theme_1
            2 -> R.color.md_theme_primary_theme_2
            3 -> R.color.md_theme_primary_theme_3
            else -> R.color.md_theme_primary
        }
    }
}