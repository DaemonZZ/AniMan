package com.daemonz.animange.ui.dialog

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daemonz.animange.R
import com.daemonz.animange.databinding.FilmInfoDialogBinding
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.ui.thememanager.AnimanTheme

class FilmInfoDialog(private val theme: AnimanTheme, private val data: ListData) :
    BaseDialog(theme) {
    companion object {
        private const val TAG = "FilmInfoDialog"
    }

    private var _binding: FilmInfoDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmInfoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent()
        dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        binding.apply {
            root.setBackgroundResource(theme.dialogBg())
            textDesc.setTextColor(theme.firstActivityTextColor(requireContext()))
            year.setTextColor(theme.firstActivityTextColor(requireContext()))
            category.setTextColor(theme.firstActivityTextColor(requireContext()))
            duration.setTextColor(theme.firstActivityTextColor(requireContext()))
            eps.setTextColor(theme.firstActivityTextColor(requireContext()))
            originalName.setTextColor(theme.firstActivityTextColor(requireContext()))
            country.setTextColor(theme.firstActivityTextColor(requireContext()))

            textDesc.text = Html.fromHtml(data.data.item?.content, Html.FROM_HTML_MODE_LEGACY)
            year.text = requireContext().getString(R.string.created_year, data.data.item?.year)
            category.text = requireContext().getString(
                R.string.category,
                data.data.item?.category?.joinToString { it.name })
            duration.text = requireContext().getString(R.string.duration, data.data.item?.time)
            eps.text = requireContext().getString(
                R.string.num_of_episode,
                data.data.item?.episodeTotal,
            )
            originalName.text =
                requireContext().getString(R.string.original_name, data.data.item?.originName)
            country.text = requireContext().getString(
                R.string.country,
                data.data.item?.country?.joinToString { it.name })

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setWidthPercent(percentage: Int = 60) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentHeight = rect.height() * percent
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, percentHeight.toInt())
    }
}