package com.example.journii_version2.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object AuthLanding : Screen("auth_landing")
    data object EmailAuth : Screen("email_auth")
    data object MobileNumber : Screen("mobile_number")
    data object OtpVerification : Screen("otp_verification")
    data object Main : Screen("main")
    data object InspirationDetail : Screen("inspiration_detail/{inspirationId}") {
        const val ARG_INSPIRATION_ID = "inspirationId"
        fun createRoute(inspirationId: String) = "inspiration_detail/$inspirationId"
    }
}

/**
 * Tabs inside the bottom navigation shell — scoped to MainScreen's own
 * NavHost, separate from the outer graph's full-screen routes above.
 */
sealed class MainTab(val route: String, val label: String) {
    data object Home : MainTab("main_home", "Home")
    data object Search : MainTab("main_search", "Search")
    data object Create : MainTab("main_create", "Create")
    data object Wishlists : MainTab("main_wishlists", "Wishlists")
    data object Profile : MainTab("main_profile", "Profile")

    companion object {
        val ALL = listOf(Home, Search, Create, Wishlists, Profile)
    }
}
