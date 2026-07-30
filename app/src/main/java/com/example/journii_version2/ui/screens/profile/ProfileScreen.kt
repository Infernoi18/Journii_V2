package com.example.journii_version2.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.journii_version2.core.data.profile.ProfileRepository
import com.example.journii_version2.core.model.Privacy
import com.example.journii_version2.core.model.UserProfile
import com.example.journii_version2.feature.profile.ProfileTab
import com.example.journii_version2.feature.profile.ProfileViewModel
import com.example.journii_version2.feature.profile.ProfileViewModelFactory
import com.example.journii_version2.ui.components.InspirationCard
import com.example.journii_version2.ui.theme.JourniiSpacing

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileRepository: ProfileRepository,
    onInspirationClick: (String) -> Unit,
    onEditProfileClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(profileRepository))
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.profile

    if (uiState.isLoading || profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = onEditProfileClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                    IconButton(onClick = onSignOutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = JourniiSpacing.sm,
                end = JourniiSpacing.sm,
                bottom = JourniiSpacing.lg
            ),
            horizontalArrangement = Arrangement.spacedBy(JourniiSpacing.sm),
            verticalItemSpacing = JourniiSpacing.sm,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                ProfileHeader(profile = profile)
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                ProfileTabRow(selectedTab = uiState.selectedTab, onTabSelected = viewModel::selectTab)
            }

            if (uiState.visibleInspirations.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = emptyStateMessage(uiState.selectedTab),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(JourniiSpacing.md)
                    )
                }
            } else {
                items(uiState.visibleInspirations, key = { it.id }) { inspiration ->
                    InspirationCard(
                        inspiration = inspiration,
                        onClick = { onInspirationClick(inspiration.id) },
                        onLikeClick = {},
                        onSaveClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(profile: UserProfile) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            profile.coverImageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(modifier = Modifier.padding(JourniiSpacing.md)) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (profile.avatarUrl != null) {
                    AsyncImage(
                        model = profile.avatarUrl,
                        contentDescription = profile.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Text(
                        text = profile.displayName.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(JourniiSpacing.sm))

            Text(text = profile.displayName, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "@${profile.username} · ${profile.privacy.displayName()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            profile.bio?.let {
                Spacer(modifier = Modifier.height(JourniiSpacing.xs))
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(JourniiSpacing.md))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ProfileStat(label = "Followers", value = profile.followersCount)
                ProfileStat(label = "Following", value = profile.followingCount)
                ProfileStat(label = "Countries", value = profile.visitedCountriesCount)
                ProfileStat(label = "Likes", value = profile.totalLikes)
                ProfileStat(label = "Saves", value = profile.totalSaves)
            }
        }
    }
}

@Composable
fun ProfileStat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), style = MaterialTheme.typography.titleMedium)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileTabRow(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    val tabs = ProfileTab.entries
    TabRow(selectedTabIndex = tabs.indexOf(selectedTab)) {
        tabs.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.displayName()) }
            )
        }
    }
}

fun ProfileTab.displayName(): String = when (this) {
    ProfileTab.PINNED -> "Pinned"
    ProfileTab.COPIED -> "Copied"
    ProfileTab.DRAFTS -> "Drafts"
}

fun Privacy.displayName(): String = when (this) {
    Privacy.PUBLIC -> "Public"
    Privacy.FOLLOWERS_ONLY -> "Followers Only"
    Privacy.PRIVATE -> "Private"
}

fun emptyStateMessage(tab: ProfileTab): String = when (tab) {
    ProfileTab.PINNED -> "Nothing pinned yet. Pin inspirations you love from the feed."
    ProfileTab.COPIED -> "You haven't copied any inspirations yet."
    ProfileTab.DRAFTS -> "No drafts in progress."
}
