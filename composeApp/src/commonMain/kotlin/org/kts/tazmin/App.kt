package org.kts.tazmin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinApplication
import org.kts.tazmin.feature.auth.di.authModule
import org.kts.tazmin.feature.auth.presentation.ui.LoginScreen
import org.kts.tazmin.feature.auth.presentation.ui.OnboardingScreen
import org.kts.tazmin.feature.courses.di.coursesModule
import org.kts.tazmin.feature.courses.presentation.ui.AllCoursesScreen
import org.kts.tazmin.feature.courses.presentation.ui.CoursesScreen
import org.kts.tazmin.navigation.Screen
import org.kts.tazmin.theme.CatTheme

@Composable
@Preview
fun App() {
    CatTheme {
        KoinApplication(
            application = {
                modules(
                    authModule,
                    coursesModule
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Welcome.route,
                ) {
                    composable(Screen.Welcome.route) {
                        OnboardingScreen(
                            onLoginClick = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Welcome.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onNavigateToMain = {
                                navController.navigate(Screen.Courses.route) {
                                    // чтоб не возвращаться назад
                                    popUpTo(Screen.Login.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Courses.route) {
                        CoursesScreen(
                            onAllCoursesClick = {
                                navController.navigate(Screen.AllCourses.route)
                            },
                            onCourseClick = { courseId ->
                                //
                            }
                        )
                    }

                    composable(Screen.AllCourses.route) {
                        AllCoursesScreen(
                            onCourseClick = { courseId ->
                                //
                            }
                        )
                    }
                }
            }
        }
    }
}
