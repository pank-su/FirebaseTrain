package su.pank.firebase_train.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import su.pank.firebase_train.screen.CartScreen
import su.pank.firebase_train.screen.CheckOutScreen
import su.pank.firebase_train.screen.ConfirmedScreen
import su.pank.firebase_train.screen.MenuScreen
import su.pank.firebase_train.viewmodel.UserViewModel

@Composable
fun UserNav() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = "MenuScreen"){
        composable("MenuScreen"){
            MenuScreen(userViewModel, navController)
        }
        composable("Cart"){
            CartScreen(userViewModel, navController)
        }
        composable("CheckOutScreen"){
            CheckOutScreen(userViewModel, navController)
        }
        composable("ConfirmedScreen"){
            ConfirmedScreen(navController = navController)
        }
    }
}