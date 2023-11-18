package su.pank.firebase_train.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import su.pank.firebase_train.screen.AuthScreen
import su.pank.firebase_train.screen.MainScreen

@Composable
fun GeneralNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "AuthScreen"){
        composable("AuthScreen"){
            AuthScreen(navController)
        }
        composable("MainScreen"){
            MainScreen()
        }
    }
}