package org.kts.tazmin.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Courses : Screen

    @Serializable
    data object Catalog : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data class CoursesDetail(val courseId: Int) : Screen
}
