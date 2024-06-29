package com.daemonz.animange.fragment.player

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentCommentBinding
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.User
import com.daemonz.animange.ui.adapter.CommentAdapter
import com.daemonz.animange.viewmodel.HomeViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel

class CommentFragment :
    BaseFragment<FragmentCommentBinding, HomeViewModel>(FragmentCommentBinding::inflate),
    ChildPlayerFragmentActions {
    override val viewModel: HomeViewModel by viewModels()
    private var playerViewModel: PlayerViewModel? = null
    private var adapter: CommentAdapter? = null
    val list = listOf(
        Comment(
            id = "123",
            content = "Hello may cung",
            user = User(
                id = "123",
                name = "Thang",
                image = 7
            ),
            createdAt = 0,
            bestReply = null,
            repliesCount = 0,
            replyFor = null,
            liked = emptyList()
        ),
        Comment(
            id = "1223",
            content = "Hello may cung21212",
            user = User(
                id = "1231",
                name = "Thangss",
                image = 9
            ),
            createdAt = 0,
            bestReply = null,
            repliesCount = 0,
            replyFor = null,
            liked = emptyList()
        )
    )

    override fun setupViews() {
        adapter = CommentAdapter { c, i -> }
        binding.recyclerComment.adapter = adapter
        adapter?.setData(list)
        binding.apply {
            edtComment.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                btnSend.isVisible = hasFocus
            }
            btnSend.setOnClickListener {
                //send comment
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                edtComment.clearFocus()
            }
        }
    }

    override fun setupObservers() {

    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }
}