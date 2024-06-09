package com.daemonz.animange.base

fun interface OnItemClickListener<Item> {
    fun onItemClick(item: Item, index: Int)
}