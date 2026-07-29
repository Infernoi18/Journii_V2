package com.example.journii_version2.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.journii_version2.core.security.googleauth.GoogleAuthManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun AuthLandingScreen(
    onSignUpWithEmail: () -> Unit,
    onLoginWithEmail: () -> Unit,
    onContinueWithMobile: () -> Unit,
    onGoogleIdToken: (String) -> Unit,
    onGoogleSignInError: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isGoogleSignInInProgress by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = "Journii", style = MaterialTheme.typography.displaySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Plan journeys worth remembering.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onSignUpWithEmail, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Up with Email")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = onLoginWithEmail, modifier = Modifier.fillMaxWidth()) {
            Text("Log In with Email")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = onContinueWithMobile, modifier = Modifier.fillMaxWidth()) {
            Text("Continue with Mobile Number")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                if (!isGoogleSignInInProgress) {
                    isGoogleSignInInProgress = true
                    coroutineScope.launch {
                        try {
                            val idToken = GoogleAuthManager(context).signIn()
                            onGoogleIdToken(idToken)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            onGoogleSignInError("Google sign-in was cancelled or failed. Please try again.")
                        } finally {
                            isGoogleSignInInProgress = false
                        }
                    }
                }
            },
            enabled = !isGoogleSignInInProgress,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isGoogleSignInInProgress) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Continue with Google")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
