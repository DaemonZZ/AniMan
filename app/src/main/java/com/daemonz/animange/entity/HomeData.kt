package com.daemonz.animange.entity

import com.daemonz.animange.base.NetworkEntity

data class HomeData(
    val items: List<Item>
) : NetworkEntity()
