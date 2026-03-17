package org.kts.tazmin.feature.profile.presentation.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.about
import ktskotlinproject.composeapp.generated.resources.followers_label
import ktskotlinproject.composeapp.generated.resources.information
import ktskotlinproject.composeapp.generated.resources.joined_prefix
import ktskotlinproject.composeapp.generated.resources.knowledge_label
import ktskotlinproject.composeapp.generated.resources.loading
import ktskotlinproject.composeapp.generated.resources.private_profile
import ktskotlinproject.composeapp.generated.resources.profile_visibility
import ktskotlinproject.composeapp.generated.resources.public_profile
import ktskotlinproject.composeapp.generated.resources.rank_template
import ktskotlinproject.composeapp.generated.resources.reputation_label
import ktskotlinproject.composeapp.generated.resources.solved_steps_label
import ktskotlinproject.composeapp.generated.resources.statistics_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.kts.tazmin.feature.courses.presentation.ui.ErrorView
import org.kts.tazmin.feature.profile.domain.model.Name
import org.kts.tazmin.feature.profile.domain.model.User
import org.kts.tazmin.feature.profile.domain.model.UserStats
import org.kts.tazmin.feature.profile.presentation.state.ProfileUiState
import org.kts.tazmin.feature.profile.presentation.viewmodel.ProfileViewModel
import org.kts.tazmin.theme.CatTheme


@Composable
fun ProfileScreen(
    //onSettingsClick: () -> Unit = {},
    //onEditProfileClick: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateToLogin: () -> Unit
) {

    LaunchedEffect(viewModel) {
        Napier.d("ProfileScreen composed")
        viewModel.loadProfile()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is ProfileUiState.LogoutSuccess) {
            onNavigateToLogin()
        }
    }
    when (val currentState = state) {

        ProfileUiState.Loading -> {
            ProfileLoading()
        }

        is ProfileUiState.Success -> {
            ProfileContent(
                user = currentState.user,
                isRefreshing = currentState.isRefreshing,
                error = currentState.error,
                isFromCache = currentState.isFromCache,
                onRefresh = { viewModel.refreshProfile() },
                onLogout = { viewModel.logout() },
                onErrorDismiss = { viewModel.clearError() }
            )
        }

        is ProfileUiState.Error -> {
            ErrorView(
                error = currentState.message,
                onReload = { viewModel.loadProfile() },
            )
        }

        is ProfileUiState.LogoutSuccess -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ProfileLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: User,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    error: String?,
    isFromCache: Boolean,
    onErrorDismiss: () -> Unit
) {

    val pullState = rememberPullToRefreshState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            )
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                ProfileHeaderSection(
                    user,
                    onLogout = onLogout
                )
            }

            user.bio?.takeIf { it.isNotBlank() }?.let {
                item {
                    ProfileBioCard(it)
                }
            }

            item {
                ProfileStatsCard(user.stats)
            }

            item {
                ProfileMetaCard(
                    joinedAt = user.joinedAt,
                    isPrivate = user.isPrivate
                )
            }

        }

        PullToRefreshContainer(
            state = pullState,
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean
) {

    AnimatedVisibility(
        visible = state.distanceFraction > 0f || isRefreshing,
        modifier = modifier
    ) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
        ) {

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {

                if (isRefreshing) {

                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )

                } else {

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }

        }

    }
}

@Composable
fun ProfileMetaCard(
    joinedAt: String,
    isPrivate: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = stringResource(Res.string.information),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = stringResource(Res.string.joined_prefix),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = joinedAt,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = if (isPrivate) Icons.Default.Lock else Icons.Default.Public,
                    contentDescription = null,
                    tint = if (isPrivate)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = stringResource(Res.string.profile_visibility),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (isPrivate) stringResource(Res.string.private_profile)
                        else stringResource(Res.string.public_profile),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

        }

    }
}

@Composable
fun ProfileBioCard(bio: String) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = stringResource(Res.string.about),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }

            HorizontalDivider()

            Text(
                text = bio,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

        }

    }
}

@Composable
fun ProfileStatsCard(stats: UserStats) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = stringResource(Res.string.statistics_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {

                StatItem(
                    icon = Icons.Default.School,
                    label = stringResource(Res.string.knowledge_label),
                    value = stats.knowledge.toString(),
                    rank = stats.knowledgeRank,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                StatItem(
                    icon = Icons.Default.Star,
                    label = stringResource(Res.string.reputation_label),
                    value = stats.reputation.toString(),
                    rank = stats.reputationRank,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                StatItem(
                    icon = Icons.Default.Group,
                    label = stringResource(Res.string.followers_label),
                    value = stats.followers.toString(),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                StatItem(
                    icon = Icons.Default.CheckCircle,
                    label = stringResource(Res.string.solved_steps_label),
                    value = stats.solvedSteps.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    rank: Int? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        rank?.takeIf { it > 0 }?.let {
            Text(
                text = stringResource(Res.string.rank_template, it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileHeaderSection(
    user: User,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
    ) {
        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(
                avatarUrl = user.avatarUrl,
                initials = user.name.initials
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user.name.full,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold
            )

            if (user.isPrivate) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Private account",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    avatarUrl: String?,
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(96.dp)
            .shadow(10.dp, CircleShape)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        // почему-то не подгружаются аватарка с api
//        if (avatarUrl != null) {
//            AsyncImage(
//                model = avatarUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//        } else {
//            Text(
//                text = initials,
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
    }
}

@Composable
@Preview
fun ProfileScreenPreview() {
    CatTheme {
        val testUser = User(
            id = 1,
            name = Name(
                first = "Иван",
                last = "Петров"
            ),
            avatarUrl = "https://i.pravatar.cc/300?u=1",
            bio = "Android разработчик",
            stats = UserStats(
                knowledge = 1250,
                knowledgeRank = 42,
                reputation = 3500,
                reputationRank = 15,
                followers = 128,
                solvedSteps = 234
            ),
            joinedAt = "15 января 2023",
            isPrivate = false
        )

        ProfileContent(
            user = testUser,
            isRefreshing = false,
            onRefresh = {},
            onLogout = { },
            error = null,
            isFromCache = false,
            onErrorDismiss = {  },
        )
    }
}

