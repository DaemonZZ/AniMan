package com.daemonz.animange.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<Item, ViewBinding : androidx.viewbinding.ViewBinding>(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<ViewBinding>>() {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding

    protected val TAG = this::class.java.simpleName
    private var data = listOf<Item>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BaseViewHolder<ViewBinding> {
        return BaseViewHolder(bindingInflater.invoke(
            LayoutInflater.from(parent.context), parent, false
        ).apply {
            bindFirstTime(this)
        })
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        val item = data[position]
        holder.binding.apply {
            bindView(this, item, position)
            root.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    protected open fun bindFirstTime(binding: ViewBinding) {}

    protected open fun bindView(binding: ViewBinding, item: Item, position: Int) {}

    @SuppressLint("NotifyDataSetChanged")
    open fun setData(data: List<Item>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getData() = data

    fun getItem(position: Int): Item? = runCatching { data[position] }.getOrNull()

    open class BaseViewHolder<ViewBinding : androidx.viewbinding.ViewBinding>(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}

interface OnItemClickListener {
    fun onItemClick(index: Int)
}