package com.daemonz.animange.util

object AppMode {
    var currentMode: AppModeEnum = AppModeEnum.Movies
}

enum class AppModeEnum {
    Movies,
    Manga
}