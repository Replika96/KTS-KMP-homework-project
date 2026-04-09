package org.kts.tazmin.feature.profile.presentation.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import ktskotlinproject.composeapp.generated.resources.private_profile
import ktskotlinproject.composeapp.generated.resources.profile_visibility
import ktskotlinproject.composeapp.generated.resources.public_profile
import ktskotlinproject.composeapp.generated.resources.rank_template
import ktskotlinproject.composeapp.generated.resources.reputation_label
import ktskotlinproject.composeapp.generated.resources.solved_steps_label
import ktskotlinproject.composeapp.generated.resources.statistics_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.kts.tazmin.core.common.toUiMessage
import org.kts.tazmin.feature.catalog.presentation.ui.ErrorView
import org.kts.tazmin.feature.profile.domain.model.Name
import org.kts.tazmin.feature.profile.domain.model.Profile
import org.kts.tazmin.feature.profile.domain.model.UserStats
import org.kts.tazmin.feature.profile.presentation.state.ProfileUiState
import org.kts.tazmin.feature.profile.presentation.viewmodel.ProfileViewModel
import org.kts.tazmin.theme.CatTheme


@Composable
fun ProfileScreen(
    //onSettingsClick: () -> Unit = {},
    //onEditProfileClick: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel<ProfileViewModel>(),
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
            ProfileLoadingView()
        }

        is ProfileUiState.Success -> {
            ProfileContent(
                profile = currentState.user,
                isRefreshing = currentState.isRefreshing,
                onRefresh = { viewModel.refreshProfile() },
                onLogout = { viewModel.logout() }
            )
        }

        is ProfileUiState.Error -> {
            ErrorView(
                error = currentState.message.toUiMessage(),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    profile: Profile,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
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
                    profile,
                    onLogout = onLogout
                )
            }

            profile.bio?.takeIf { it.isNotBlank() }?.let {
                item {
                    ProfileBioCard(it)
                }
            }

            item {
                ProfileStatsCard(profile.stats)
            }

            item {
                ProfileMetaCard(
                    joinedAt = profile.joinedAtFormatted,
                    isPrivate = profile.isPrivate
                )
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
                    tint = MaterialTheme.colorScheme.tertiary
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
                        MaterialTheme.colorScheme.secondary
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
                    tint = MaterialTheme.colorScheme.secondary
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
                    iconTint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                StatItem(
                    icon = Icons.Default.Star,
                    label = stringResource(Res.string.reputation_label),
                    value = stats.reputation.toString(),
                    rank = stats.reputationRank,
                    iconTint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                StatItem(
                    icon = Icons.Default.Group,
                    label = stringResource(Res.string.followers_label),
                    value = stats.followers.toString(),
                    iconTint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                StatItem(
                    icon = Icons.Default.CheckCircle,
                    label = stringResource(Res.string.solved_steps_label),
                    value = stats.solvedSteps.toString(),
                    iconTint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
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
    iconTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
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
    profile: Profile,
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
                        MaterialTheme.colorScheme.tertiaryContainer
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
                avatarUrl = profile.avatarUrl,
                initials = profile.name.initials
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = profile.name.full,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold
            )

            if (profile.isPrivate) {
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
        // c сервака приходит svg и я не знаю, как отображать его в kmp пока что
//        if (avatarUrl != null) {
//            AsyncImage(
//                model = avatarUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//        }
    }
}

@Composable
@Preview
fun ProfileScreenPreview() {
    CatTheme {
        val testUser = Profile(
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
            profile = testUser,
            isRefreshing = false,
            onRefresh = {},
            onLogout = { }
        )
    }
}

@Composable
fun ProfileLoadingView() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // header Section Skeleton
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f) // Обновили цвет
                            )
                        )
                    )
            ) {
                // logout button skeleton
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // avatar skeleton
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = alpha))
                            .shadow(10.dp, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // name skeleton
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = alpha * 0.5f))
                    )

                    // private account badge skeleton
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = alpha * 0.3f))
                            .padding(top = 6.dp)
                    )
                }
            }
        }

        // bio Card Skeleton
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = alpha * 0.5f))
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // title skeleton
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                        )
                    }

                    HorizontalDivider()

                    // bio text skeleton
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (it == 2) 0.7f else 1f)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = alpha * 0.3f
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

        // stats Card Skeleton
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // title skeleton
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                    )

                    // stats row skeleton
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        repeat(4) { index ->
                            StatItemSkeleton(
                                alpha = alpha,
                                iconColor = when (index) {
                                    0 -> MaterialTheme.colorScheme.primary
                                    1 -> MaterialTheme.colorScheme.tertiary
                                    2 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                }
                            )

                            if (index < 3) {
                                VerticalDivider(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(1.dp)
                                        .background(
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.1f
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        // meta Card Skeleton
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // title skeleton
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                    )

                    // joined date skeleton
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = alpha * 0.5f))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = alpha * 0.3f
                                        )
                                    )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                            )
                        }
                    }

                    HorizontalDivider()

                    // visibility skeleton
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = alpha * 0.5f))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = alpha * 0.3f
                                        )
                                    )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItemSkeleton(
    alpha: Float,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // icon skeleton
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = alpha * 0.5f))
        )

        Spacer(modifier = Modifier.height(4.dp))

        // value skeleton
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
        )

        Spacer(modifier = Modifier.height(2.dp))

        // label skeleton
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha * 0.3f))
        )
    }
}
