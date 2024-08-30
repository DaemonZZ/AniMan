package com.daemonz.animange.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.ui.thememanager.AnimanTheme


abstract class BaseRecyclerAdapter<Item, ViewBinding : androidx.viewbinding.ViewBinding>(
    private val onItemClickListener: OnItemClickListener<Item>
) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<ViewBinding>>() {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding
    protected var imgDomain = BuildConfig.IMG_BASE_URL

    protected val TAG = this::class.java.simpleName
    var data = mutableListOf<Item>()
        private set

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BaseViewHolder<ViewBinding> {
        val binding = bindingInflater.invoke(
            LayoutInflater.from(parent.context), parent, false
        ).apply {
            bindFirstTime(this)
        }
        setupLayout(binding, parent)
        return BaseViewHolder(binding)
    }

    open fun setupLayout(binding: ViewBinding, parent: ViewGroup) {

    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        val item = data[position]
        holder.binding.apply {
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            bindView(this, item, position)
        }
    }

    override fun getItemCount(): Int = data.size

    protected open fun bindFirstTime(binding: ViewBinding) {}

    abstract fun bindView(binding: ViewBinding, item: Item, position: Int)

    @SuppressLint("NotifyDataSetChanged")
    open fun setData(data: List<Item>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun setData(data: List<Item>, imgDomain: String) {
        this.imgDomain = imgDomain
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Item? = runCatching { data[position] }.getOrNull()

    open class BaseViewHolder<ViewBinding : androidx.viewbinding.ViewBinding>(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    open fun syncTheme(theme: AnimanTheme) {}
}