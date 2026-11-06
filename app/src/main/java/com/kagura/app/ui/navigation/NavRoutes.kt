package com.kagura.app.ui.navigation

object NavRoutes {
    const val WELCOME = "welcome"
    const val GACHA = "gacha"
    const val DEX = "dex"
    const val SEARCH = "search"
    const val PROFILE = "profile"
    const val DETAIL = "detail/{id}"
    fun detail(id: Int) = "detail/$id"
}
