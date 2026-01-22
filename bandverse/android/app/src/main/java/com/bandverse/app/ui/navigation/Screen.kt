package com.bandverse.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "首页")
    object History : Screen("history", "历史")
    object Create : Screen("create", "创建推荐")
    object Profile : Screen("profile", "我的")
}
