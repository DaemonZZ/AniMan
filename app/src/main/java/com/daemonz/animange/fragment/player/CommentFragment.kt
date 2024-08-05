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
import com.daemonz.animange.util.loadImageFromStorage
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
        binding.apply {
            LoginData.getActiveUser()?.image?.let {
                imgAvt.loadImageFromStorage(it)
            }
            textLayout.isEndIconVisible = false
            edtComment.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                textLayout.isEndIconVisible = hasFocus
                if (!hasFocus && edtComment.text.isNullOrEmpty()) {
                    playerViewModel?.waitingReplyFor = null
                }
            }
            textLayout.setEndIconOnClickListener {
                if (binding.edtComment.text.toString().trim().isEmpty()) {
                    return@setEndIconOnClickListener
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
                    replyFor = playerViewModel?.waitingReplyFor
                )
                playerViewModel?.sendComment(comment)
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
        playerViewModel?.apply {
            comments.observe(viewLifecycleOwner) {
                binding.recyclerComment.isVisible = it.isNotEmpty()
                binding.textNoComment.isVisible = it.isEmpty()
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

    override fun syncTheme() {
        super.syncTheme()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        adapter = CommentAdapter(
            loadReplies = { parent, pos ->
                ALog.d(TAG, "loadReplies: $parent $pos")
                playerViewModel?.loadReplies(parent, pos)
            },
            onReplyClicked = { item, _ ->
                ALog.d(TAG, "onReplyClicked: $item")
                playerViewModel?.waitingReplyFor = item.replyFor ?: item.id
                binding.edtComment.requestFocus()
                imm?.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
            },
            onLikeClicked = { item, pos ->
                ALog.d(TAG, "onLikeClicked: $item $pos")
                playerViewModel?.toggleLike(item)
            },
            onClickItem = { _, _ ->
                binding.edtComment.clearFocus()
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
            },
            theme = currentTheme
        )
        binding.recyclerComment.adapter = adapter
        binding.edtComment.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
        playerViewModel?.comments?.value?.let {
            binding.recyclerComment.isVisible = it.isNotEmpty()
            binding.textNoComment.isVisible = it.isEmpty()
            adapter?.setData(it)
        }
    }
}