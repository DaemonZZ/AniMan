package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemChooseUserBinding
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.loadImageFromStorage

class ChooseUserAdapter(
    onItemClickListener: OnItemClickListener<User>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<User, ItemChooseUserBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemChooseUserBinding
        get() = ItemChooseUserBinding::inflate

    private var isEditMode: Boolean = false

    override fun bindView(binding: ItemChooseUserBinding, item: User, position: Int) {
        binding.apply {
            checkbox.isInvisible = !item.isActive
            if (item.userType == UserType.ADD) {
                avatar.setImageResource(R.drawable.ic_add)
                userName.text = root.context.getString(R.string.add_user)
            } else if (item.userType == UserType.EMPTY) {
                avatar.alpha = 0f
            } else {
                avatar.loadImageFromStorage(item.image ?: 1)
                userName.text = item.name
                if (isEditMode) {
                    avatar.alpha = 0.4f
                    editOverlay.isVisible = true
                } else {
                    avatar.alpha = 1f
                    editOverlay.isVisible = false
                }
            }
        }
    }

    fun toggleEditMode(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        notifyDataSetChanged()
    }

    fun isEditModeEnabled(): Boolean {
        return isEditMode
    }

    fun setActive(id: String) {
        var old = -1
        var new = -1
        data.forEachIndexed { index, user ->
            if (user.isActive) old = index
            if (user.id == id) {
                user.isActive = true
                new = index
            } else {
                user.isActive = false
            }
        }
        if (new >= 0 && old >= 0) {
            notifyItemChanged(old)
            notifyItemChanged(new)
        }
    }
}