package com.daemonz.animange.fragment.player

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.databinding.FragmentCommentBinding
import com.daemonz.animange.databinding.ItemCommentBinding
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.User
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.CommentAdapter
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.CommentViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class CommentFragment :
    BaseFragment<FragmentCommentBinding, CommentViewModel>(FragmentCommentBinding::inflate),
    ChildPlayerFragmentActions {
    override val viewModel: CommentViewModel by viewModels()
    private var playerViewModel: PlayerViewModel? = null
    private var adapter: CommentAdapter? = null

    override fun setupViews() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        adapter = CommentAdapter(
            loadReplies = { parent, pos ->
                ALog.d(TAG, "loadReplies: $parent $pos")
                viewModel.loadReplies(parent, pos)
            },
            onReplyClicked = { item, _ ->
                ALog.d(TAG, "onReplyClicked: $item")
                viewModel.waitingReplyFor = item.replyFor ?: item.id
                binding.edtComment.requestFocus()
                imm?.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
            },
            onClickItem = { _, _ -> }
        )
        binding.recyclerComment.adapter = adapter
        binding.apply {
            edtComment.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                btnSend.isVisible = hasFocus
                if (!hasFocus) {
                    viewModel.waitingReplyFor = null
                }
            }
            btnSend.setOnClickListener {
                if (binding.edtComment.text.toString().trim().isEmpty()) {
                    return@setOnClickListener
                }
                val user = LoginData.getActiveUser()?.let {
                    User(
                        id = it.id,
                        name = it.name,
                        image = it.image
                    )
                } ?: User()
                val comment = Comment(
                    id = UUID.randomUUID().toString(),
                    slug = playerViewModel?.playerData?.value?.data?.item?.slug.toString(),
                    content = binding.edtComment.text.toString().trim(),
                    user = user,
                    createdAt = System.currentTimeMillis(),
                    replyFor = viewModel.waitingReplyFor
                )
                viewModel.sendComment(comment)
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                edtComment.clearFocus()
                edtComment.text?.clear()
            }
            if (LoginData.account == null) {
                edtComment.isEnabled = false
                edtComment.clearFocus()
                textLayout.hint = getString(R.string.login_to_comment)
            } else {
                edtComment.isEnabled = true
                textLayout.hint = getString(R.string.comment_hint)
            }
        }
    }

    override fun setupObservers() {
        viewModel.apply {
            comments.observe(viewLifecycleOwner) {
                adapter?.setData(it)
            }
            onRepliesLoaded.observe(viewLifecycleOwner) { map ->
                ALog.d(TAG, "onRepliesLoaded: $map")
                map?.entries?.firstOrNull()?.let {
                    val view = binding.recyclerComment.findViewHolderForAdapterPosition(it.key)
                    val binding =
                        (view as? BaseRecyclerAdapter.BaseViewHolder<ItemCommentBinding>)?.binding
                    binding?.let { bd ->
                        adapter?.loadReply(it.value, bd)
                    }

                }
            }
        }
    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }

    override fun initData() {
        viewModel.loadComments(playerViewModel?.playerData?.value?.data?.item?.slug.toString())
    }
}