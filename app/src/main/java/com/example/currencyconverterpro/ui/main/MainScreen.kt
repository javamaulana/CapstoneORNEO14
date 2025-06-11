package com.example.currencyconverterpro.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.currencyconverterpro.ui.ViewModelFactory

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Converter : BottomBarScreen("converter", "Converter", Icons.Default.SwapHoriz)
    object Favorites : BottomBarScreen("favorites", "Favorites", Icons.Default.Favorite)
    object Catalog : BottomBarScreen("catalog", "Katalog", Icons.Default.ListAlt)
    object Profile : BottomBarScreen("profile", "Profile", Icons.Default.Person)
}

val bottomBarScreens = listOf(
    BottomBarScreen.Converter,
    BottomBarScreen.Favorites,
    BottomBarScreen.Catalog,
    BottomBarScreen.Profile,
)

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val navController = rememberNavController()
    val factory = ViewModelFactory(LocalContext.current)

    // ViewModel dibuat sekali di sini untuk dibagikan ke semua layar di dalam NavHost ini
    val converterViewModel: ConverterViewModel = viewModel(factory = factory)
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = factory)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val detailViewModel: CurrencyDetailViewModel = viewModel(factory = factory)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomBarScreens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route?.startsWith(screen.route) == true } == true,

                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Converter.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = "${BottomBarScreen.Converter.route}?from={from}&to={to}",
                arguments = listOf(
                    navArgument("from") { type = NavType.StringType; nullable = true },
                    navArgument("to") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val fromArg = backStackEntry.arguments?.getString("from")
                val toArg = backStackEntry.arguments?.getString("to")
                ConverterScreen(viewModel = converterViewModel, fromArg = fromArg, toArg = toArg)
            }

            composable(BottomBarScreen.Favorites.route) {
                FavoritesScreen(navController = navController, viewModel = favoritesViewModel)
            }

            composable(BottomBarScreen.Catalog.route) {
                CurrencyCatalogScreen(navController = navController, viewModel = converterViewModel)
            }

            composable(
                route = "currency_detail/{currencyCode}",
                arguments = listOf(navArgument("currencyCode") { type = NavType.StringType })
            ) { backStackEntry ->
                val currencyCode = backStackEntry.arguments?.getString("currencyCode")

                val allCurrencies by converterViewModel.currencies.collectAsState()

                if (currencyCode != null) {
                    CurrencyDetailScreen(
                        currencyCode = currencyCode,
                        navController = navController,
                        viewModel = detailViewModel,
                        allCurrencies = allCurrencies
                    )
                }
            }

            composable(BottomBarScreen.Profile.route) {
                ProfileScreen(mainNavController, viewModel = profileViewModel)
            }
        }
    }
}