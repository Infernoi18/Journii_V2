package com.example.journii_version2.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.journii_version2.core.data.inspiration.InspirationRepository
import com.example.journii_version2.core.data.profile.ProfileRepository
import com.example.journii_version2.navigation.MainTab
import com.example.journii_version2.ui.screens.feed.FeedScreen
import com.example.journii_version2.ui.screens.profile.ProfileScreen
import com.example.journii_version2.ui.screens.search.SearchScreen

/**
 * Owns its own NavHost/NavController scoped to the five bottom-nav tabs,
 * separate from the outer JourniiNavGraph. Full-screen destinations like
 * Inspiration Detail live in the outer graph (via onInspirationClick), so
 * they push over this shell without the bottom bar following them.
 */
@Composable
fun MainScreen(
    inspirationRepository: InspirationRepository,
    profileRepository: ProfileRepository,
    onInspirationClick: (String) -> Unit
) {
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = { JourniiBottomNavigationBar(tabNavController) }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = MainTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainTab.Home.route) {
                FeedScreen(
                    repository = inspirationRepository,
                    onInspirationClick = onInspirationClick
                )
            }
            composable(MainTab.Search.route) {
                SearchScreen(
                    repository = inspirationRepository,
                    onInspirationClick = onInspirationClick
                )
            }
            composable(MainTab.Create.route) {
                ComingSoonTab(label = "Create")
            }
            composable(MainTab.Wishlists.route) {
                ComingSoonTab(label = "Wishlists")
            }
            composable(MainTab.Profile.route) {
                ProfileScreen(
                    profileRepository = profileRepository,
                    onInspirationClick = onInspirationClick
                )
            }
        }
    }
}

@Composable
private fun JourniiBottomNavigationBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar {
        MainTab.ALL.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = { navController.navigateToTab(tab) },
                icon = { TabIcon(tab) },
                label = { Text(tab.label) }
            )
        }
    }
}

@Composable
private fun TabIcon(tab: MainTab) {
    val glyph = when (tab) {
        MainTab.Home -> "🏠"
        MainTab.Search -> "🔍"
        MainTab.Create -> "＋"
        MainTab.Wishlists -> "♡"
        MainTab.Profile -> "👤"
    }
    Text(text = glyph)
}

@Composable
private fun ComingSoonTab(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$label is coming in a future batch", style = MaterialTheme.typography.bodyLarge)
    }
}

/** Standard single-top, state-preserving tab navigation. */
private fun NavHostController.navigateToTab(tab: MainTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
