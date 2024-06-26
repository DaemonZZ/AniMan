package com.daemonz.animange.entity

import java.util.Date

data class UpdateData(
    val version: String = "",
    @field:JvmField
    val isOptional: Boolean = false,
    val time: Date? = null,
)