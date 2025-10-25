package com.daemonz.animange.fragment.manga

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentReadMangaBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.MangaPageAdapter
import com.daemonz.animange.viewmodel.manga.ReadMangaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadMangaFragment : BaseFragment<FragmentReadMangaBinding, ReadMangaViewModel>(
    FragmentReadMangaBinding::inflate
) {
    override val viewModel: ReadMangaViewModel by activityViewModels()
    private val args: ReadMangaFragmentArgs by navArgs()
    private var adapter: MangaPageAdapter? = null
    override fun initData() {
        viewModel.getManga(args.item)
    }

    override fun setupViews() {
        binding.apply {
            adapter = MangaPageAdapter({ _, _ ->
                toggleActionBar(!topBar.isVisible)
            })
            mangaContent.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            mangaContent.adapter = adapter
            (activity as MainActivity).supportActionBar?.setBackgroundDrawable(
                ContextCompat.getColor(requireContext(), R.color.bg_dark).toDrawable()
            )
            mangaContent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    toggleActionBar(false)
                }
            })
            btnList.setOnClickListener {
                findNavController().navigate(ReadMangaFragmentDirections.actionReadMangaFragmentToMangaDetailFragment())
            }
        }
    }

    override fun setupObservers() {
        viewModel.chapterDisplayData.observe(viewLifecycleOwner) {
            ALog.d(TAG, "setupObservers: $it")
            adapter?.setData(it.imageList)
        }
    }
    private fun toggleActionBar(isShown: Boolean) {
        binding.apply {
            if (isShown) {
                topBar.animate().translationY(0f).alpha(0.9f).setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            topBar.isVisible = true
                        }
                    })
                botBar.animate().translationY(0f).alpha(0.9f).setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            botBar.isVisible = true
                        }
                    })
            } else {
                topBar.animate().translationY(-topBar.height.toFloat()).alpha(0f).setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            topBar.isVisible = false
                        }
                    })
                botBar.animate().translationY(botBar.height.toFloat()).alpha(0f).setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            botBar.isVisible = false
                        }
                    })
            }
        }
    }
}