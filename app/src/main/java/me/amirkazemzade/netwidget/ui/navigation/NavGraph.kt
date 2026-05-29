package me.amirkazemzade.netwidget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.amirkazemzade.netwidget.ui.AuthStateViewModel
import me.amirkazemzade.netwidget.ui.dashboard.DashboardScreen
import me.amirkazemzade.netwidget.ui.login.LoginScreen
import me.amirkazemzade.netwidget.ui.splash.SplashScreen

@Composable
fun NavGraph(
    authStateViewModel: AuthStateViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()

    // Handles initial routing at startup based on auth, or when the auth state changes like login/logout
    LaunchedEffect(Unit) {
        authStateViewModel.authState.collect { route ->
            if (navController.currentDestination?.route != route) {
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
    }
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen()
        }
        composable(Screen.Login.route) {
            LoginScreen()
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}
