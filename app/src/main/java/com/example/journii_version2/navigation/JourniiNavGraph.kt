package com.example.journii_version2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.data.profile.ProfileRepository
import com.example.journii_version2.core.data.wishlist.WishlistRepository
import com.example.journii_version2.core.security.session.SecureTokenStore
import com.example.journii_version2.feature.auth.AuthViewModel
import com.example.journii_version2.feature.auth.AuthViewModelFactory
import com.example.journii_version2.ui.screens.auth.AuthLandingScreen
import com.example.journii_version2.ui.screens.auth.EmailAuthScreen
import com.example.journii_version2.ui.screens.auth.MobileNumberScreen
import com.example.journii_version2.ui.screens.auth.OtpVerificationScreen
import com.example.journii_version2.ui.screens.create.ItineraryBuilderScreen
import com.example.journii_version2.ui.screens.create.OptionalSectionsScreen
import com.example.journii_version2.ui.screens.inspiration.InspirationDetailScreen
import com.example.journii_version2.ui.screens.main.MainScreen
import com.example.journii_version2.ui.screens.splash.SplashScreen
import com.example.journii_version2.ui.screens.wishlist.WishlistDetailScreen

private const val AUTH_GRAPH_ROUTE = "auth_graph"

@Composable
fun JourniiNavGraph(
    secureTokenStore: SecureTokenStore,
    inspirationRepository: InspirationRepository,
    profileRepository: ProfileRepository,
    wishlistRepository: WishlistRepository,
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
                    navController.navigate(Screen.Main.route) {
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
                    onSignUpWithEmail = {
                        authViewModel.setSignUpMode(true)
                        navController.navigate(Screen.EmailAuth.route)
                    },
                    onLoginWithEmail = {
                        authViewModel.setSignUpMode(false)
                        navController.navigate(Screen.EmailAuth.route)
                    },
                    onContinueWithMobile = { navController.navigate(Screen.MobileNumber.route) },
                    onGoogleIdToken = { idToken -> authViewModel.completeGoogleSignIn(idToken) },
                    onGoogleSignInError = { message -> authViewModel.onGoogleSignInError(message) }
                )
            }

            composable(Screen.EmailAuth.route) { backStackEntry ->
                val authViewModel = sharedAuthViewModel(navController, backStackEntry, secureTokenStore)
                EmailAuthScreen(
                    viewModel = authViewModel,
                    onAuthenticated = { navigateToMain(navController) }
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
                    onAuthenticated = { navigateToMain(navController) }
                )
            }
        }

        composable(Screen.Main.route) {
            MainScreen(
                inspirationRepository = inspirationRepository,
                profileRepository = profileRepository,
                wishlistRepository = wishlistRepository,
                onInspirationClick = { id -> navController.navigate(Screen.InspirationDetail.createRoute(id)) },
                onWishlistClick = { id -> navController.navigate(Screen.WishlistDetail.createRoute(id)) }
            )
        }

        composable(
            route = Screen.InspirationDetail.route,
            arguments = listOf(
                navArgument(Screen.InspirationDetail.ARG_INSPIRATION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspirationId = backStackEntry.arguments
                ?.getString(Screen.InspirationDetail.ARG_INSPIRATION_ID)
                ?: return@composable
            InspirationDetailScreen(
                repository = inspirationRepository,
                profileRepository = profileRepository,
                inspirationId = inspirationId,
                onBackClick = { navController.popBackStack() },
                onEditItineraryClick = { id -> navController.navigate(Screen.ItineraryBuilder.createRoute(id)) },
                onCopyCompleted = { newId ->
                    navController.navigate(Screen.InspirationDetail.createRoute(newId)) {
                        popUpTo(Screen.InspirationDetail.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ItineraryBuilder.route,
            arguments = listOf(
                navArgument(Screen.ItineraryBuilder.ARG_INSPIRATION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspirationId = backStackEntry.arguments
                ?.getString(Screen.ItineraryBuilder.ARG_INSPIRATION_ID)
                ?: return@composable
            ItineraryBuilderScreen(
                repository = inspirationRepository,
                inspirationId = inspirationId,
                onBackClick = { navController.popBackStack() },
                onContinueClick = { id -> navController.navigate(Screen.OptionalSections.createRoute(id)) }
            )
        }

        composable(
            route = Screen.OptionalSections.route,
            arguments = listOf(
                navArgument(Screen.OptionalSections.ARG_INSPIRATION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspirationId = backStackEntry.arguments
                ?.getString(Screen.OptionalSections.ARG_INSPIRATION_ID)
                ?: return@composable
            OptionalSectionsScreen(
                repository = inspirationRepository,
                inspirationId = inspirationId,
                onBackClick = { navController.popBackStack() },
                onPublished = {
                    navController.navigate(Screen.InspirationDetail.createRoute(inspirationId)) {
                        popUpTo(Screen.Main.route)
                    }
                }
            )
        }

        composable(
            route = Screen.WishlistDetail.route,
            arguments = listOf(
                navArgument(Screen.WishlistDetail.ARG_WISHLIST_ID) { type = NavType.StringType }
            )
        ) {
            WishlistDetailScreen(
                onBackClick = { navController.popBackStack() },
                onInspirationClick = { id -> navController.navigate(Screen.InspirationDetail.createRoute(id)) }
            )
        }
    }
}

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

private fun navigateToMain(navController: NavHostController) {
    navController.navigate(Screen.Main.route) {
        popUpTo(AUTH_GRAPH_ROUTE) { inclusive = true }
    }
}
