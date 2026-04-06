package org.kts.tazmin.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.kts.tazmin.feature.auth.presentation.ui.OnboardingScreen
import org.kts.tazmin.feature.auth.presentation.ui.SplashScreen
import org.kts.tazmin.feature.auth.presentation.viewmodel.AppStartViewModel
import org.kts.tazmin.feature.courses.presentation.ui.CatalogScreen
import org.kts.tazmin.feature.courses.presentation.ui.CoursesScreen
import org.kts.tazmin.feature.profile.presentation.ui.ProfileScreen
import org.kts.tazmin.navigation.Screen
import org.kts.tazmin.theme.CatTheme

@Composable
fun App(
    appStartViewModel: AppStartViewModel = koinInject()
) {
    CatTheme {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomNav = currentDestination?.let { destination ->
                    destination.hasRoute<Screen.Courses>() ||
                            destination.hasRoute<Screen.Catalog>() ||
                            destination.hasRoute<Screen.Profile>()
                } ?: false

                if (showBottomNav) {
                    BottomNavBar(navController)
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash,
                ) {
                    composable<Screen.Splash> {
                        val startDestination by appStartViewModel.startDestination.collectAsStateWithLifecycle()

                        SplashScreen()

                        LaunchedEffect(startDestination) {
                            startDestination?.let{ destination ->
                                delay(300)
                                navController.navigate(destination) {
                                    popUpTo<Screen.Splash> {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    composable<Screen.Onboarding> {
                        OnboardingScreen(
                            onNavigateToMain = {
                                appStartViewModel.completeOnboarding()
                                navController.navigate(Screen.Courses) {
                                    popUpTo<Screen.Onboarding> {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable<Screen.Courses> {
                        CoursesScreen(
                            onCatalogClick = {
                                navController.navigate(Screen.Catalog)
                            },
                            onCourseClick = { courseId ->
                                navController.navigate(
                                    Screen.CoursesDetail(courseId)
                                )
                            }
                        )
                    }

                    composable<Screen.Catalog> {
                        CatalogScreen(
                            onCourseClick = { courseId ->
                                navController.navigate(Screen.CoursesDetail(courseId))
                            }
                        )
                    }

                    composable<Screen.Profile> {
                        ProfileScreen(
                            //onSettingsClick = {},
                            //onEditProfileClick = {},
                            onNavigateToLogin = {
                                navController.navigate(Screen.Onboarding) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                    composable<Screen.CoursesDetail> { backStackEntry ->
                        val args = backStackEntry.toRoute<Screen.CoursesDetail>()
                    }
                }
            }
        }
    }
}
