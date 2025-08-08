package com.daemonz.animange.fragment.manga

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentReadMangaBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.viewmodel.manga.ReadMangaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadMangaFragment : BaseFragment<FragmentReadMangaBinding, ReadMangaViewModel>(
    FragmentReadMangaBinding::inflate
) {
    override val viewModel: ReadMangaViewModel by viewModels()
    private val args: ReadMangaFragmentArgs by navArgs()
    override fun initData() {
        viewModel.getManga(args.item)
    }

    override fun setupViews() {

    }

    override fun setupObservers() {
        viewModel.chapterDisplayData.observe(viewLifecycleOwner) {
            ALog.d(TAG, "setupObservers: $it")
        }
    }
}