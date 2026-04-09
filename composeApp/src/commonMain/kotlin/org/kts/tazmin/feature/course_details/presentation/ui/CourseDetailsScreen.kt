package org.kts.tazmin.feature.course_details.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.tab_info
import ktskotlinproject.composeapp.generated.resources.tab_modules
import ktskotlinproject.composeapp.generated.resources.tab_reviews
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.kts.tazmin.feature.course_details.presentation.state.CourseDetailsUiState
import org.kts.tazmin.feature.course_details.presentation.ui.components.CourseCTAComponent
import org.kts.tazmin.feature.course_details.presentation.ui.components.CourseHeaderComponent
import org.kts.tazmin.feature.course_details.presentation.ui.components.CourseInfoComponent
import org.kts.tazmin.feature.course_details.presentation.ui.components.CourseModulesComponent
import org.kts.tazmin.feature.course_details.presentation.ui.components.TitleSkeleton
import org.kts.tazmin.feature.course_details.presentation.ui.components.skeleton.CTASkeletonLoading
import org.kts.tazmin.feature.course_details.presentation.ui.components.skeleton.InfoSkeletonLoading
import org.kts.tazmin.feature.course_details.presentation.viewmodel.CourseDetailsViewModel
import org.kts.tazmin.feature.course_reviews.presentation.ui.rememberReviewState
import org.kts.tazmin.feature.course_reviews.presentation.ui.reviewsContent

@Composable
fun CourseDetailsScreen(
    courseId: Int,
    onBack: () -> Unit
) {
    val viewModel: CourseDetailsViewModel = koinViewModel(
        parameters = { parametersOf(courseId) }
    )

    val state by viewModel.uiState.collectAsState()

    val scrollState = rememberLazyListState()
    var selectedTab by remember { mutableStateOf(0) }

    val reviewHolder = rememberReviewState(courseId)

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxSize()
    ) {

        item {
            CourseTopSection(
                state = state,
                onBack = onBack
            )
        }

        stickyHeader {
            CourseTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        when (selectedTab) {

            0 -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    state.info?.let {
                        CourseInfoComponent(it)
                    } ?: InfoSkeletonLoading()
                }
            }

            1 -> {
                reviewsContent(
                    state = reviewHolder.state,
                    summary = reviewHolder.summary,
                    onVote = { id, vote ->
                        reviewHolder.viewModel.vote(id, vote)
                    },
                    onLoadNextPage = {
                        reviewHolder.viewModel.loadNextPage()
                    },
                    onRetry = {
                        reviewHolder.viewModel.refresh()
                    },
                    onFilterSelected = {
                        reviewHolder.viewModel.setScore(it)
                    }
                )
            }

            2 -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    CourseModulesComponent(
                        modules = state.modules,
                        onLessonClick = {}
                    ) //ModulesSkeletonLoading todo
                }
            }
        }
    }
}
/*
@Composable
fun ScrollToTopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Scroll to top"
        )
    }
}*/

@Composable
private fun CourseTopSection(
    state: CourseDetailsUiState,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
            .heightIn(max = 250.dp)
    ) {

        // background cover
        state.info?.coverUrl?.let { cover ->
            AsyncImage(
                model = cover,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(0.30.dp)
                    .alpha(0.25f)
            )
        }

        Column {

            // header
            if (state.header != null) {
                CourseHeaderComponent(
                    header = state.header,
                    onBack = onBack,
                    onToggleFavorite = {},
                    onShare = {}
                )
            } else {
                TitleSkeleton()
            }

            // cta
            state.cta?.let { cta ->
                CourseCTAComponent(
                    cta = cta,
                    onEnrollClick = {},
                    onContinueClick = {},
                    enrolledCount = state.info?.learnersCount ?: 0
                )
            } ?: CTASkeletonLoading()
        }
    }
}

@Composable
fun CourseTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        stringResource(Res.string.tab_info),
        stringResource(Res.string.tab_reviews),
        stringResource(Res.string.tab_modules)
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }
    }
}
