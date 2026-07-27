package com.example.journii_version2.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.journii_version2.core.security.session.SecureTokenStore
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    secureTokenStore: SecureTokenStore,
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        val loggedIn = secureTokenStore.isLoggedIn.first()
        if (loggedIn) onNavigateToHome() else onNavigateToAuth()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Journii", style = MaterialTheme.typography.headlineMedium)
    }
}
