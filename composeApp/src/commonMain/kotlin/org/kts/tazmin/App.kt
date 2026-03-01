package org.kts.tazmin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.kts.tazmin.feature.auth.di.authModule
import org.kts.tazmin.navigation.Screen
import org.kts.tazmin.feature.auth.presentation.ui.LoginScreen
import org.kts.tazmin.feature.auth.presentation.ui.WelcomeScreen
import org.kts.tazmin.feature.feed.di.feedModule
import org.kts.tazmin.feature.feed.presentation.ui.FeedScreen
import org.kts.tazmin.theme.CatTheme

@Composable
@Preview
fun App() {
    CatTheme {
        KoinApplication(
            application = {
                modules(authModule,
                    feedModule)
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
                        WelcomeScreen(
                            onLoginClick = {
                                navController.navigate(Screen.Login.route){
                                    popUpTo(Screen.Welcome.route){
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onNavigateToMain = {
                                navController.navigate(Screen.Feed.route){
                                    // чтоб не возвращаться назад
                                    popUpTo(Screen.Login.route){
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Feed.route) {
                        FeedScreen()
                    }
                }
            }
        }
    }
}
