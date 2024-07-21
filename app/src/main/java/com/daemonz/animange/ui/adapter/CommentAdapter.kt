package com.daemonz.animange.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemCommentBinding
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.loadImageFromStorage

class CommentAdapter(
    private val onLikeClicked: OnItemClickListener<Comment>,
    private val loadReplies: OnItemClickListener<Comment>,
    private val onReplyClicked: OnItemClickListener<Comment>,
    private val onClickItem: OnItemClickListener<Comment>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<Comment, ItemCommentBinding>(onClickItem, theme) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemCommentBinding
        get() = ItemCommentBinding::inflate
    private var slug = ""

    override fun setData(data: List<Comment>) {
        if (data.isNotEmpty()) slug = data.first().slug
        super.setData(data)
    }

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
                loadReplies.onItemClick(item, position)
            }

            textReply.setOnClickListener {
                onReplyClicked.onItemClick(item, position)
            }
            if (recyclerReply.isVisible) {
                groupReplyCompact.isVisible = false
                loadReplies.onItemClick(item, position)
            }
            LoginData.getActiveUser()?.id?.let { user ->
                if (item.liked.contains(user)) {
                    textLike.setTextColor(root.context.getColor(R.color.md_theme_primary))
                    textLike.setTypeface(null, Typeface.BOLD)
                } else {
                    textLike.setTextColor(root.context.getColor(R.color.md_theme_onSurface_highContrast))
                    textLike.setTypeface(null, Typeface.NORMAL)
                }
            }
            textLike.setOnClickListener {
                onLikeClicked.onItemClick(item, position)
            }
        }
    }

    fun loadReply(items: List<Comment>, binding: ItemCommentBinding) {
        ALog.d(TAG, "loadReply: ${items.size}")
        val adapter =
            CommentAdapter(
                onLikeClicked = onLikeClicked,
                loadReplies = loadReplies,
                onReplyClicked = onReplyClicked,
                onClickItem = onClickItem,
                theme = theme
            )
        binding.recyclerReply.adapter = adapter
        adapter.setData(items)
    }

    private fun getTextTime(time: Long, context: Context): String {
        val offsetTime = System.currentTimeMillis() - time
        val minute = (offsetTime / 1000 / 60)
        val hour = (offsetTime / 1000 / 60 / 60)
        val day = (offsetTime / 1000 / 60 / 60 / 24)
        val month = (offsetTime / 1000 / 60 / 60 / 24 / 30)
        val year = (offsetTime / 1000 / 60 / 60 / 24 / 30 / 12)
        return when {
            minute < 1 -> context.getString(R.string.now)
            hour < 1 && minute >= 1 -> context.getString(
                R.string.comment_time_minute,
                minute.toInt()
            )
            hour < 24 -> context.getString(R.string.comment_time_hour, hour.toInt())
            hour < 24 * 30 -> context.getString(R.string.comment_time_day, day.toInt())
            hour < 24 * 30 * 12 -> context.getString(R.string.comment_time_month, month.toInt())
            else -> context.getString(R.string.comment_time_year, year.toInt())
        }
    }
}