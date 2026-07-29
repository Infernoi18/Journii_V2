package com.example.journii_version2.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.journii_version2.core.data.wishlist.WishlistRepository
import com.example.journii_version2.navigation.MainTab
import com.example.journii_version2.ui.screens.create.CreateBasicInfoScreen
import com.example.journii_version2.ui.screens.feed.FeedScreen
import com.example.journii_version2.ui.screens.profile.ProfileScreen
import com.example.journii_version2.ui.screens.search.SearchScreen
import com.example.journii_version2.ui.screens.wishlist.WishlistScreen

@Composable
fun MainScreen(
    inspirationRepository: InspirationRepository,
    profileRepository: ProfileRepository,
    wishlistRepository: WishlistRepository,
    onInspirationClick: (String) -> Unit,
    onWishlistClick: (String) -> Unit
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
                CreateBasicInfoScreen(
                    repository = inspirationRepository,
                    onDraftCreated = onInspirationClick
                )
            }
            composable(MainTab.Wishlists.route) {
                WishlistScreen(
                    onWishlistClick = onWishlistClick
                )
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

private fun NavHostController.navigateToTab(tab: MainTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
