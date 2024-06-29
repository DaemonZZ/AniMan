package com.daemonz.animange.fragment.player

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentCommentBinding
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.User
import com.daemonz.animange.ui.adapter.CommentAdapter
import com.daemonz.animange.util.LoginData
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
            bestReply = Comment(
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
            repliesCount = 1,
            replyFor = null,
            liked = listOf("1", "2")
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

    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }
}