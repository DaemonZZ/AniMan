package com.daemonz.animange.entity

import com.daemonz.animange.base.NetworkEntity

data class ListData(
    val data: Data,
    val status: String = "",
    val message: String = ""
):NetworkEntity()
