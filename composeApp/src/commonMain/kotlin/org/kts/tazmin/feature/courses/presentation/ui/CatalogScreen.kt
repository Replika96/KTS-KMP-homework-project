package org.kts.tazmin.feature.courses.presentation.ui


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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.all_courses
import ktskotlinproject.composeapp.generated.resources.free
import ktskotlinproject.composeapp.generated.resources.search_courses
import ktskotlinproject.composeapp.generated.resources.students
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.kts.tazmin.feature.courses.domain.entity.Course
import org.kts.tazmin.feature.courses.presentation.state.CoursesUiEvent
import org.kts.tazmin.feature.courses.presentation.viewmodel.CoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CoursesViewModel = koinInject(),
    onCourseClick: (Int) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(viewModel) {
        if (state.courses.isEmpty() && !state.isLoading) {
            viewModel.handleEvent(CoursesUiEvent.LoadCourses)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.all_courses)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.handleEvent(CoursesUiEvent.SearchQueryChanged(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(Res.string.search_courses)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                isError = searchState.error != null && state.searchQuery.isNotBlank()
            )

            if (searchState.error != null && state.searchQuery.isNotBlank()) {
                Text(
                    text = searchState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            val isSearchActive = state.searchQuery.length >= 2
            val isLoading = if (isSearchActive) searchState.isSearching else state.isLoading
            val items = if (isSearchActive) searchState.results else state.courses
            val error = if (isSearchActive) searchState.error else state.coursesError

            when {
                // начальная загрузка
                isLoading && items.isEmpty() -> {
                    CatalogLoadingView()
                }

                // ошибка и нет данных (не из кэша)
                error != null && items.isEmpty() && !state.isFromCache && !searchState.isFromCache -> {
                    ErrorView(
                        error = error,
                        onReload = {
                            if (isSearchActive) {
                                // повторный поиск
                                viewModel.handleEvent(CoursesUiEvent.SearchQueryChanged(state.searchQuery))
                            } else {
                                viewModel.handleEvent(CoursesUiEvent.LoadCourses)
                            }
                        }
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        /*if (!isSearchActive && state.showCachedBanner && state.cachedInfoMessage != null) {
                            CachedInfoBanner(message = state.cachedInfoMessage!!)
                        }

                        if (!isSearchActive && error != null && state.isFromCache) {
                            ErrorBanner(message = error, onDismiss = { viewModel.clearError() })
                        }

                        if (isSearchActive && searchState.showCachedBanner && searchState.cachedInfoMessage != null) {
                            CachedInfoBanner(message = searchState.cachedInfoMessage!!)
                        }

                        if (isSearchActive && error != null && searchState.isFromCache) {
                            ErrorBanner(message = error, onDismiss = { viewModel.clearSearchError() })
                        }*/
                        PullToRefreshBox(
                            isRefreshing = state.isRefreshing && !isSearchActive,
                            onRefresh = {
                                if (!isSearchActive) {
                                    viewModel.handleEvent(CoursesUiEvent.RefreshCourses)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                itemsIndexed(
                                    items = items,
                                    key = { index, course -> "${course.id}_$index" }
                                ) { _, course ->
                                    CourseCatalogItem(
                                        course = course,
                                        onClick = { onCourseClick(course.id) }
                                    )
                                }

                                // индикатор пагинации обычного списка
                                if (!isSearchActive && state.isLoadingMore) {
                                    item(key = "loading_more") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }

                                // индикатор пагинации поиска
                                if (isSearchActive && searchState.isLoadingMore) {
                                    item(key = "search_loading_more") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }

                                // индикатор начального поиска (если уже есть результаты)
                                if (isSearchActive && searchState.isSearching && items.isNotEmpty()) {
                                    item(key = "search_loading") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                            }
                        }

                        LaunchedEffect(listState, isSearchActive, state.hasNext, searchState.hasNext) {
                            snapshotFlow {
                                // индекс последнего видимого элемента
                                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                            }
                                .distinctUntilChanged()
                                .collect { lastVisibleIndex ->
                                    val lastIndex = items.lastIndex
                                    if (lastVisibleIndex != null && lastIndex >= 0) {
                                        val shouldLoadMore = lastVisibleIndex >= lastIndex - 2
                                        if (shouldLoadMore) {
                                            if (!isSearchActive) {
                                                if (state.hasNext && !state.isLoadingMore && !state.isLoading && !state.isRefreshing) {
                                                    viewModel.handleEvent(CoursesUiEvent.LoadMoreCourses)
                                                }
                                            } else {
                                                if (searchState.hasNext && !searchState.isLoadingMore && !searchState.isSearching) {
                                                    viewModel.handleEvent(CoursesUiEvent.LoadMoreSearchResults)
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CachedInfoBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun CatalogLoadingView() {
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(8) { _ ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // image skeleton
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.5f))
                        )

                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                        )

                        Spacer(Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                        )

                        Spacer(Modifier.height(8.dp))

                        // author skeleton
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                        )

                        Spacer(Modifier.height(8.dp))

                        // rating, students, price row skeleton
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp, 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                            )

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(60.dp, 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.3f))
                            )

                            Spacer(Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(40.dp, 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha * 0.5f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCatalogItem(
    course: Course,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            AsyncImage(
                model = course.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = course.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = course.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ ${course.rating}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "${course.studentsCount} ${stringResource(Res.string.students)}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = if (course.isPaid && course.price != null)
                            course.price
                        else
                            stringResource(Res.string.free),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
