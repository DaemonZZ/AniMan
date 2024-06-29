package com.daemonz.animange.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemCommentBinding
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.User
import com.daemonz.animange.util.loadImageFromStorage

class CommentAdapter(onClickItem: OnItemClickListener<Comment>) :
    BaseRecyclerAdapter<Comment, ItemCommentBinding>(onClickItem) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemCommentBinding
        get() = ItemCommentBinding::inflate

    override fun bindView(binding: ItemCommentBinding, item: Comment, position: Int) {
        binding.apply {
            textName.text = item.user.name
            textContent.text = item.content
            imgAvt.loadImageFromStorage(item.user.image ?: 1)
            textTime.text = getTextTime(item.createdAt, root.context)
            if (item.liked.isNotEmpty()) {
                textLikeCount.text = item.liked.size.toString()
                textLikeCount.isVisible = true
            } else {
                textLikeCount.isVisible = false
            }
            if (item.repliesCount > 0) {
                imgAvtNewRep.loadImageFromStorage(item.bestReply?.user?.image ?: 1)
                textNewestRep.text = root.context.getString(
                    R.string.comment_newest_reply,
                    item.bestReply?.user?.name
                )
                groupReplyCompact.isVisible = true
            } else {
                groupReplyCompact.isVisible = false
                recyclerReply.isVisible = false
            }
            textNewestRep.setOnClickListener {
                groupReplyCompact.isVisible = false
                recyclerReply.isVisible = true
                loadReply(item, this)
            }
        }
    }

    private fun loadReply(item: Comment, binding: ItemCommentBinding) {
        val adapter = CommentAdapter() { _, _ -> }
        binding.recyclerReply.adapter = adapter
        val dummy = listOf(
            Comment(
                id = "123",
                content = "Hello may cung rep",
                user = User(
                    id = "2223",
                    name = "Thang Pro",
                    image = 3
                ),
                createdAt = 1719659613000L,
                bestReply = null,
                repliesCount = 0,
                replyFor = "1223",
                liked = emptyList()
            ),
            Comment(
                id = "12322",
                content = "Hello may cung rep22",
                user = User(
                    id = "2223",
                    name = "Thang Pro",
                    image = 3
                ),
                createdAt = 1719659613000L,
                bestReply = null,
                repliesCount = 0,
                replyFor = "1223",
                liked = emptyList()
            )
        )
        adapter.setData(dummy)
    }

    private fun getTextTime(time: Long, context: Context): String {
        val offsetTime = System.currentTimeMillis() - time
        val minute = (offsetTime / 1000 / 60)
        val hour = (offsetTime / 1000 / 60 / 60)
        val day = (offsetTime / 1000 / 60 / 60 / 24)
        val month = (offsetTime / 1000 / 60 / 60 / 24 / 30)
        val year = (offsetTime / 1000 / 60 / 60 / 24 / 30 / 12)
        return when {
            hour < 1 -> context.getString(R.string.comment_time_minute, minute.toInt())
            hour < 24 -> context.getString(R.string.comment_time_hour, hour.toInt())
            hour < 24 * 30 -> context.getString(R.string.comment_time_day, day.toInt())
            hour < 24 * 30 * 12 -> context.getString(R.string.comment_time_month, month.toInt())
            else -> context.getString(R.string.comment_time_year, year.toInt())
        }
    }
}