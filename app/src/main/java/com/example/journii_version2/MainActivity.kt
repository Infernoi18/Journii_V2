package com.example.journii_version2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.journii_version2.navigation.JourniiNavGraph
import com.example.journii_version2.navigation.Screen
import com.example.journii_version2.ui.theme.JourniiTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val appContainer = (application as JourniiApplication).appContainer
        val secureTokenStore = appContainer.secureTokenStore

        var startDestination by mutableStateOf<String?>(null)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                secureTokenStore.isLoggedIn
                    .onEach { isLoggedIn ->
                        startDestination = if (isLoggedIn) Screen.Main.route else "auth_graph"
                    }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            startDestination == null
        }

        enableEdgeToEdge()

        setContent {
            JourniiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val destination = startDestination
                    if (destination != null) {
                        JourniiNavGraph(
                            startDestination = destination,
                            secureTokenStore = appContainer.secureTokenStore,
                            inspirationRepository = appContainer.inspirationRepository,
                            profileRepository = appContainer.profileRepository,
                            wishlistRepository = appContainer.wishlistRepository
                        )
                    }
                }
            }
        }
    }
}
