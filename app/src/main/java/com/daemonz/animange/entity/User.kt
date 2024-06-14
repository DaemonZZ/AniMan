package com.daemonz.animange.entity

import androidx.annotation.Keep
import com.daemonz.animange.R

@Keep
data class User(
    val name: String? = null,
    val userType: UserType? = UserType.CHILD,
    val image: Int? = null,
    @field:JvmField
    val isMainUser: Boolean = false,
    @field:JvmField
    var isActive: Boolean = false
) {
    fun getImgResource(): Int {
        return when (image) {
            1 -> R.drawable.avt_1
            2 -> R.drawable.avt_2
            3 -> R.drawable.avt_3
            4 -> R.drawable.avt_4
            5 -> R.drawable.avt_5
            6 -> R.drawable.avt_6
            7 -> R.drawable.avt_7
            8 -> R.drawable.avt_8
            9 -> R.drawable.avt_9
            10 -> R.drawable.avt_10
            11 -> R.drawable.avt_11
            12 -> R.drawable.avt_12
            13 -> R.drawable.avt_13
            14 -> R.drawable.avt_14
            15 -> R.drawable.avt_15
            16 -> R.drawable.avt_16
            17 -> R.drawable.avt_17
            18 -> R.drawable.avt_18
            19 -> R.drawable.avt_19
            20 -> R.drawable.avt_20
            else -> R.drawable.avt_1
        }
    }
}

@Keep
enum class UserType {
    ADULT,
    CHILD,
    ADD,
}