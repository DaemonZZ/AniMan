package com.daemonz.animange.base

import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData


abstract class BasePagingAdapter<T, Item, ViewBinding : androidx.viewbinding.ViewBinding>(
    private val onItemClickListener: OnItemClickListener<Item>
) : BaseRecyclerAdapter<Item, ViewBinding>(onItemClickListener) {
    var firstPage = 1
        private set
    var lastPage = 1
        private set
    var lastPosition = 0
        private set

    override fun setData(data: List<Item>, imgDomain: String) {
        if (this.data.isEmpty()) {
            super.setData(data, imgDomain)
        } else {
            val page = (data.firstOrNull() as? PagingData<T>)?.page ?: return
            if (page < firstPage) {
                this.data.addAll(0, data)
                if (lastPage > firstPage + 1) {
                    this.data.removeIf {
                        (it as? PagingData<T>)?.page == lastPage
                    }
                    lastPage--
                }
                firstPage = page
                lastPosition = data.size
            } else if (page > lastPage) {
                this.data.addAll(this.data.size, data)
                if (firstPage < lastPage - 1) {
                    this.data.removeIf { (it as? PagingData<T>)?.page == firstPage }
                    firstPage++
                }
                lastPosition = this.data.size - data.size
                lastPage = page

            }
            notifyDataSetChanged()
        }
    }
}