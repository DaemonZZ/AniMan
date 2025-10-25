package com.daemonz.animange.fragment.manga

import android.annotation.SuppressLint
import android.text.Html
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentMangaDetailBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.PlayerPagerAdapter
import com.daemonz.animange.ui.dialog.FilmInfoDialog
import com.daemonz.animange.util.makeTextLink
import com.daemonz.animange.util.setImageFromUrl
import com.daemonz.animange.util.toListData
import com.daemonz.animange.viewmodel.manga.ReadMangaViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MangaDetailFragment : BaseFragment<FragmentMangaDetailBinding, ReadMangaViewModel>(
    FragmentMangaDetailBinding::inflate
) {
    override val viewModel: ReadMangaViewModel by activityViewModels()
    private var pagerAdapter: PlayerPagerAdapter? = null
    private val listFrag = listOf<Fragment>(
        SuggestionMangaFragment(),
        ChaptersFragment(),
//        CommentMangaFragment(),
//        RatingsMangaFragment()
    )

    override fun setupViews() {
        binding.apply {
            pagerAdapter = PlayerPagerAdapter(listFrag, this@MangaDetailFragment)
            viewPager.adapter = pagerAdapter
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(tabSuggest, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.suggest)
                    1 -> tab.text = getString(R.string.episodes)
                    2 -> tab.text = getString(R.string.comment)
                    3 -> tab.text = getString(R.string.rating)
                }
            }.attach()
            tabSuggest.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    (activity as? MainActivity)?.showHideSystemBar(false)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
        }
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            root.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabSuggest.setBackgroundColor(
                currentTheme.firstActivityBackgroundColor(
                    requireContext()
                )
            )
            tabSuggest.setTabTextColors(
                currentTheme.tabTextColorDefault(requireContext()),
                currentTheme.tabTextColorSelected(requireContext())
            )
            textTitle.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            textDesc.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            rateAvg.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            btnRate.setIconResource(currentTheme.iconRate())
            btnShare.setIconResource(currentTheme.iconShare())
        }
    }

    override fun setupObservers() {
        viewModel.currentManga?.let {
            ALog.d(TAG, "url: ${it.data.getImageUrl()}")
            binding.mangaThumb.setImageFromUrl(it.data.getImageUrl())
            makeDecsText()
            binding.textTitle.text = requireContext().getString(
                R.string.reader_title,
                it.data.item?.name,
                it.data.item?.chapters?.lastOrNull()?.serverData?.lastOrNull()?.slug
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun makeDecsText() {
        binding.apply {
            val desc = Html.fromHtml(
                viewModel.currentManga?.data?.item?.content,
                Html.FROM_HTML_MODE_LEGACY
            )
            if (desc.length > 150) {
                textDesc.text = desc.substring(
                    0,
                    125
                ) + getString(R.string.three_dot) + getString(R.string.expand_text)
            } else {
                textDesc.text = desc.toString()
                    .trim() + getString(R.string.three_dot) + getString(R.string.expand_text)
            }
            textDesc.makeTextLink(
                textLink = getString(R.string.expand_text),
                underline = true,
                bold = true,
                color = ContextCompat.getColor(requireContext(), R.color.button_light),
                onClick = {
                    viewModel.currentManga?.let {
                        FilmInfoDialog(currentTheme, it.toListData()).show(
                            childFragmentManager,
                            TAG
                        )
                    }
                }
            )
        }
    }
}