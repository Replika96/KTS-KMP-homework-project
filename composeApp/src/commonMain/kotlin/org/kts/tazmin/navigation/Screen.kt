package org.kts.tazmin.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen(route = "welcome")
    data object Login : Screen(route = "login")
    data object Courses : Screen(route = "courses")
    object AllCourses : Screen("all_courses")
}
