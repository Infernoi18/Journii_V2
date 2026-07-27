package com.example.journii_version2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.journii_version2.navigation.JourniiNavGraph
import com.example.journii_version2.ui.theme.Journii_Version2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val secureTokenStore = (application as JourniiApplication).appContainer.secureTokenStore

        setContent {
            Journii_Version2Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JourniiNavGraph(secureTokenStore = secureTokenStore)
                }
            }
        }
    }
}
