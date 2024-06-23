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
import com.daemonz.animange.util.loadImageFromStorage

class ChooseUserAdapter(
    onItemClickListener: OnItemClickListener<User>,
) :
    BaseRecyclerAdapter<User, ItemChooseUserBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemChooseUserBinding
        get() = ItemChooseUserBinding::inflate

    private var isEditMode: Boolean = false

    override fun bindView(binding: ItemChooseUserBinding, item: User, position: Int) {
        binding.apply {
            checkbox.isInvisible = !item.isActive
            if (item.userType == UserType.ADD) {
                avatar.setImageResource(R.drawable.ic_add)
                userName.text = root.context.getString(R.string.add_user)
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
}