package org.kts.tazmin.navigation

sealed class Screen(val route: String) {
    data object Welcome: Screen("welcome")
    data object Login: Screen("login")
}