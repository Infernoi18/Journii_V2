package com.example.journii_version2.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object AuthLanding : Screen("auth_landing")
    data object EmailAuth : Screen("email_auth")
    data object MobileNumber : Screen("mobile_number")
    data object OtpVerification : Screen("otp_verification")
    data object Home : Screen("home")
}
