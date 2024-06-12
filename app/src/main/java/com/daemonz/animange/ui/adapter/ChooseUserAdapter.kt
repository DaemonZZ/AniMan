package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemChooseUserBinding
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.util.setImageFromUrl

class ChooseUserAdapter(onItemClickListener: OnItemClickListener<User>) :
    BaseRecyclerAdapter<User, ItemChooseUserBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemChooseUserBinding
        get() = ItemChooseUserBinding::inflate

    override fun bindView(binding: ItemChooseUserBinding, item: User, position: Int) {
        binding.apply {
            checkbox.isInvisible = !item.isActive
            if (item.userType == UserType.ADD) {
                avatar.setImageResource(R.drawable.ic_add)
                userName.text = root.context.getString(R.string.add_user)
            } else {
                avatar.setImageFromUrl(
                    item.imageUrl.toString(),
                    placeHolder = R.drawable.avt_1,
                    error = R.drawable.avt_1
                )
                userName.text = item.name
            }
        }
    }
}