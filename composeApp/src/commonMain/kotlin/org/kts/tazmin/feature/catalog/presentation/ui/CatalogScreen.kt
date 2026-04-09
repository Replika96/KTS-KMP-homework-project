package org.kts.tazmin.feature.catalog.presentation.ui


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.course_placeholder
import ktskotlinproject.composeapp.generated.resources.courses_few
import ktskotlinproject.composeapp.generated.resources.courses_many
import ktskotlinproject.composeapp.generated.resources.courses_one
import ktskotlinproject.composeapp.generated.resources.free
import ktskotlinproject.composeapp.generated.resources.search_courses
import ktskotlinproject.composeapp.generated.resources.students
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.kts.tazmin.core.common.toUiMessage
import org.kts.tazmin.feature.catalog.domain.entity.CatalogSection
import org.kts.tazmin.feature.catalog.domain.entity.Course
import org.kts.tazmin.feature.catalog.presentation.state.CatalogUiState
import org.kts.tazmin.feature.catalog.presentation.state.SearchUiState
import org.kts.tazmin.feature.catalog.presentation.viewmodel.CatalogViewModel
import org.kts.tazmin.feature.catalog.presentation.viewmodel.SearchCoursesViewModel

@Composable
fun CatalogScreen(
    catalogViewModel: CatalogViewModel = koinViewModel(),
    searchCoursesViewModel: SearchCoursesViewModel = koinViewModel(),
    onCourseClick: (Int) -> Unit = {}
) {
    val catalogState by catalogViewModel.state.collectAsStateWithLifecycle()
    val searchState by searchCoursesViewModel.state.collectAsStateWithLifecycle()

    CatalogScreenContent(
        catalogState = catalogState,
        searchState = searchState,
        onQueryChange = searchCoursesViewModel::onQueryChanged,
        onCourseClick = onCourseClick,
        onRefreshCatalog = catalogViewModel::refresh,
        onLoadMoreSearch = searchCoursesViewModel::loadMore
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CatalogScreenContent(
    catalogState: CatalogUiState,
    searchState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onCourseClick: (Int) -> Unit,
    onRefreshCatalog: () -> Unit,
    onLoadMoreSearch: () -> Unit
) {
    val listState = rememberLazyListState()
    val isSearchActive = searchState.query.length >= 2

    val isInitialLoading =
        !isSearchActive && catalogState.catalog.isEmpty() && !catalogState.hasLoadedOnce


    val currentError = when {
        isSearchActive -> searchState.error
        else -> catalogState.catalogError
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            //search bar
            OutlinedTextField(
                value = searchState.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(Res.string.search_courses)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                isError = searchState.error != null && searchState.query.isNotBlank()
            )

            //search error under field
            if (searchState.error != null &&
                searchState.query.isNotBlank() &&
                searchState.results.isEmpty()
            ) {
                Text(
                    text = searchState.error.toUiMessage(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            when {
                //initial loading
                isInitialLoading -> {
                    CatalogLoadingView()
                }

                //catalog error
                currentError != null && !isSearchActive && catalogState.catalog.isEmpty() -> {
                    ErrorView(
                        error = currentError.toUiMessage(),
                        onReload = onRefreshCatalog
                    )
                }

                else -> {
                    //main content with pull-to-refresh
                    PullToRefreshBox(
                        isRefreshing = catalogState.isRefreshing && !isSearchActive,
                        onRefresh = { if (!isSearchActive) onRefreshCatalog() },
                        modifier = Modifier.fillMaxSize()
                    ) {

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {

                            //catalog mode
                            if (!isSearchActive) {
                                catalogState.catalog.forEach { section ->

                                    if (section !is CatalogSection.Banner) {
                                        item("header_${section.id}") {
                                            SectionHeader(
                                                title = section.title,
                                                coursesCount = when (section) {
                                                    is CatalogSection.CourseList -> section.courses.size
                                                },
                                                onClick = {
                                                    // открыть список всей секции
                                                }
                                            )
                                        }
                                    }

                                    item("content_${section.id}") {
                                        CatalogSectionView(
                                            section = section,
                                            onCourseClick = onCourseClick
                                        )
                                    }
                                }
                            }

                            if (isSearchActive) {

                                items(
                                    items = searchState.results,
                                    key = { it.id }
                                ) { course ->
                                    CourseCatalogItem(
                                        course = course,
                                        onClick = { onCourseClick(course.id) }
                                    )
                                }

                                if (searchState.isLoadingMore) {
                                    item("pagination_loader") {
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
                            }
                        }
                    }
                }
            }
        }
    }

    //pagination trigger
    LaunchedEffect(isSearchActive, searchState.results.size) {
        if (!isSearchActive) return@LaunchedEffect

        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->

                val lastIndex = searchState.results.lastIndex

                val shouldLoadMore =
                    lastVisibleIndex != null &&
                            lastVisibleIndex >= lastIndex - 2 &&
                            searchState.hasNext &&
                            !searchState.isLoadingMore &&
                            !searchState.isSearching

                if (shouldLoadMore) onLoadMoreSearch()
            }
    }
}

@Composable
fun SectionHeader(
    title: String,
    coursesCount: Int?,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            if (coursesCount != null) {
                Text(
                    text = "$coursesCount",
                    //text = pluralCourses(coursesCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (onClick != null) {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun CatalogSectionView(
    section: CatalogSection,
    onCourseClick: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        when (section) {
            is CatalogSection.CourseList -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = section.courses,
                        key = { it.id }
                    ) { course ->
                        CourseCard(
                            course = course,
                            onClick = { onCourseClick(course.id) },
                            modifier = Modifier.width(270.dp)
                        )
                    }
                }
            }

            is CatalogSection.Banner -> {
                BannerCard(
                    title = section.title,
                    cover = section.cover,
                    url = section.url,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(134.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = course.coverUrl,
                contentDescription = course.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Res.drawable.course_placeholder),
                error = painterResource(Res.drawable.course_placeholder),
                fallback = painterResource(Res.drawable.course_placeholder)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = course.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = stringResource(Res.string.students),
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatStudentCount(course.studentsCount),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = if (course.isPaid && !course.price.isNullOrBlank())
                            course.price
                        else
                            stringResource(Res.string.free),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (course.isPaid)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun formatStudentCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 10_000 -> "${count / 1000}K"
        count >= 1_000 -> {
            val thousands = count / 1000
            val remainder = (count % 1000) / 100
            if (remainder > 0) "${thousands}.${remainder}K"
            else "${thousands}K"
        }

        else -> count.toString()
    }
}

@Composable
fun BannerCard(
    title: String,
    cover: String?,
    url: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // TODO: open url
            },
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = cover,
                contentDescription = title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
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
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Res.drawable.course_placeholder),
                error = painterResource(Res.drawable.course_placeholder),
                fallback = painterResource(Res.drawable.course_placeholder)
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

                Spacer(Modifier.height(4.dp))

                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

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

@Composable
fun pluralCourses(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100

    val form = when {
        mod100 in 11..14 ->
            stringResource(Res.string.courses_many, count)

        mod10 == 1 ->
            stringResource(Res.string.courses_one, count)

        mod10 in 2..4 ->
            stringResource(Res.string.courses_few, count)

        else ->
            stringResource(Res.string.courses_many, count)
    }
    return form
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CatalogScreenPreview() {

    val fakeCourses = listOf(
        Course(
            id = 1,
            title = "Kotlin Basics",
            coverUrl = null,
            studentsCount = 12000,
            description = "Learn Kotlin from scratch",
            author = "JetBrains",
            rating = 4.7,
            isPaid = false,
            price = "1000$",
            progress = 80f,
            score = 50,
            cost = 100
        ),
        Course(
            id = 2,
            title = "Jetpack Compose",
            coverUrl = null,
            studentsCount = 8500,
            description = "Modern UI toolkit",
            author = "Google",
            rating = 4.8,
            isPaid = true,
            price = "1000$",
            progress = 30f,
            score = 50,
            cost = 100
        ),
        Course(
            id = 3,
            title = "Algorithms 101",
            coverUrl = null,
            studentsCount = 5000,
            description = "Basic algorithms",
            author = "CS Academy",
            rating = 4.5,
            isPaid = false,
            price = "1000$",
            progress = 12f,
            score = 50,
            cost = 100
        )
    )

    val fakeCatalog = listOf(
        CatalogSection.CourseList(
            title = "Popular Courses",
            courses = fakeCourses,
            id = 1,
            totalCount = 1,
            courseListId = 23
        ),
        CatalogSection.Banner(
            title = "Big Sale!",
            cover = null,
            url = "https://stepik.org",
            id = 2
        )
    )

    val fakeCatalogState = CatalogUiState(
        catalog = fakeCatalog,
        isLoading = false,
        isRefreshing = false
    )

    val fakeSearchState = SearchUiState(
        query = "",
        results = emptyList(),
        isSearching = false
    )

    CatalogScreenContent(
        catalogState = fakeCatalogState,
        searchState = fakeSearchState,
        onQueryChange = {},
        onCourseClick = {},
        onRefreshCatalog = {},
        onLoadMoreSearch = {}
    )
}
