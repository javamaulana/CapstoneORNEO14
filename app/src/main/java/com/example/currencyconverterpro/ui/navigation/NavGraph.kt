package com.example.currencyconverterpro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.currencyconverterpro.ui.ViewModelFactory
import com.example.currencyconverterpro.ui.auth.LoginScreen
import com.example.currencyconverterpro.ui.auth.RegisterScreen
import com.example.currencyconverterpro.ui.main.MainScreen
import com.example.currencyconverterpro.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val CONVERTER = "converter"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
}

@Composable
fun AppNavHost(navController: NavHostController) {
    val factory = ViewModelFactory(LocalContext.current.applicationContext)

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController, viewModel = viewModel(factory = factory))
        }

        navigation(startDestination = Routes.LOGIN, route = Routes.AUTH) {
            composable(Routes.LOGIN) {
                LoginScreen(navController = navController, viewModel = viewModel(factory = factory))
            }
            composable(Routes.REGISTER) {
                RegisterScreen(navController = navController, viewModel = viewModel(factory = factory))
            }
        }

        composable(Routes.MAIN) {
            MainScreen(mainNavController = navController)
        }
    }
}