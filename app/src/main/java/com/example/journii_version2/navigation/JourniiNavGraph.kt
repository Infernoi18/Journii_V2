package com.example.journii_version2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.journii_version2.core.security.session.SecureTokenStore
import com.example.journii_version2.feature.auth.AuthViewModel
import com.example.journii_version2.feature.auth.AuthViewModelFactory
import com.example.journii_version2.ui.screens.auth.AuthLandingScreen
import com.example.journii_version2.ui.screens.auth.EmailAuthScreen
import com.example.journii_version2.ui.screens.auth.MobileNumberScreen
import com.example.journii_version2.ui.screens.auth.OtpVerificationScreen
import com.example.journii_version2.ui.screens.home.HomeScreen
import com.example.journii_version2.ui.screens.splash.SplashScreen

private const val AUTH_GRAPH_ROUTE = "auth_graph"

@Composable
fun JourniiNavGraph(
    secureTokenStore: SecureTokenStore,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                secureTokenStore = secureTokenStore,
                onNavigateToAuth = {
                    navController.navigate(AUTH_GRAPH_ROUTE) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        navigation(
            startDestination = Screen.AuthLanding.route,
            route = AUTH_GRAPH_ROUTE
        ) {
            composable(Screen.AuthLanding.route) { backStackEntry ->
                val authViewModel = sharedAuthViewModel(navController, backStackEntry, secureTokenStore)
                AuthLandingScreen(
                    onContinueWithEmail = { navController.navigate(Screen.EmailAuth.route) },
                    onContinueWithMobile = { navController.navigate(Screen.MobileNumber.route) },
                    onGoogleIdToken = { idToken -> authViewModel.completeGoogleSignIn(idToken) },
                    onGoogleSignInError = { message -> authViewModel.onGoogleSignInError(message) }
                )
            }

            composable(Screen.EmailAuth.route) { backStackEntry ->
                val authViewModel = sharedAuthViewModel(navController, backStackEntry, secureTokenStore)
                EmailAuthScreen(
                    viewModel = authViewModel,
                    onAuthenticated = { navigateToHome(navController) }
                )
            }

            composable(Screen.MobileNumber.route) { backStackEntry ->
                val authViewModel = sharedAuthViewModel(navController, backStackEntry, secureTokenStore)
                MobileNumberScreen(
                    viewModel = authViewModel,
                    onOtpSent = { navController.navigate(Screen.OtpVerification.route) }
                )
            }

            composable(Screen.OtpVerification.route) { backStackEntry ->
                val authViewModel = sharedAuthViewModel(navController, backStackEntry, secureTokenStore)
                OtpVerificationScreen(
                    viewModel = authViewModel,
                    onAuthenticated = { navigateToHome(navController) }
                )
            }
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}

/**
 * Scopes one AuthViewModel to the whole auth_graph back stack entry, so state
 * (mobile number, OTP, in-flight sign-in) survives navigating between
 * Landing -> Email/Mobile -> OTP, instead of resetting on every screen.
 */
@Composable
private fun sharedAuthViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    secureTokenStore: SecureTokenStore
): AuthViewModel {
    val parentEntry = remember(backStackEntry) {
        navController.getBackStackEntry(AUTH_GRAPH_ROUTE)
    }
    return viewModel(viewModelStoreOwner = parentEntry, factory = AuthViewModelFactory(secureTokenStore))
}

private fun navigateToHome(navController: NavHostController) {
    navController.navigate(Screen.Home.route) {
        popUpTo(AUTH_GRAPH_ROUTE) { inclusive = true }
    }
}
