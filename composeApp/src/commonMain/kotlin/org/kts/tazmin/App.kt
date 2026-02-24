package org.kts.tazmin

import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.kts.tazmin.navigation.Screen
import org.kts.tazmin.presentation.LoginScreen
import org.kts.tazmin.presentation.WelcomeScreen
import org.kts.tazmin.theme.CatTheme

@Composable
@Preview
fun App() {
    CatTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
        ){
            composable(Screen.Welcome.route){
                WelcomeScreen(
                    onLoginClick = { navController.navigate(Screen.Login.route) }
                )
            }
            composable(Screen.Login.route){
                LoginScreen()
            }
        }
    }
}