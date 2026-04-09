package org.kts.tazmin.feature.catalog.presentation.ui

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.all_courses
import ktskotlinproject.composeapp.generated.resources.completed
import ktskotlinproject.composeapp.generated.resources.in_progress
import ktskotlinproject.composeapp.generated.resources.my_courses
import ktskotlinproject.composeapp.generated.resources.no_completed_courses
import ktskotlinproject.composeapp.generated.resources.no_courses_in_progress
import ktskotlinproject.composeapp.generated.resources.no_courses_not_started
import ktskotlinproject.composeapp.generated.resources.no_courses_yet
import ktskotlinproject.composeapp.generated.resources.not_started
import ktskotlinproject.composeapp.generated.resources.search_courses
import ktskotlinproject.composeapp.generated.resources.total
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.kts.tazmin.core.common.toUiMessage
import org.kts.tazmin.feature.catalog.domain.entity.Course
import org.kts.tazmin.feature.catalog.presentation.viewmodel.MyCoursesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCoursesScreen(
    viewModel: MyCoursesViewModel = koinViewModel(),
    onCourseClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredCourses = remember(state.courses, searchQuery) {
        if (searchQuery.isBlank()) {
            state.courses
        } else {
            state.courses.filter { course ->
                course.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCourses()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.my_courses),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        when {
            // первичная загрузка
            state.isLoading && state.courses.isEmpty() -> {
                CoursesLoading(paddingValues)
            }

            // ошибка и нет данных
            state.coursesError != null && state.courses.isEmpty() -> {
                ErrorView(
                    error = state.coursesError!!.toUiMessage(), // todo
                    onReload = { viewModel.loadCourses() }
                )
            }

            // успех или есть данные
            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    AllCoursesContent(
                        courses = filteredCourses,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        paddingValues = paddingValues,
                        listState = listState,
                        onCourseClick = onCourseClick
                    )
                }
            }
        }
    }
}

@Composable
fun AllCoursesContent(
    courses: List<Course>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    paddingValues: PaddingValues,
    listState: LazyListState,
    onCourseClick: (Int) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(Filter.ALL) }

    val filteredByStatus = remember(courses, selectedFilter) {
        when (selectedFilter) {
            Filter.ALL -> courses
            Filter.IN_PROGRESS -> courses.filter {
                it.progress != null && it.progress in 0f..0.99f && it.progress > 0f
            }

            Filter.COMPLETED -> courses.filter { it.progress == 1f }
            Filter.NOT_STARTED -> courses.filter { it.progress == 0f || it.progress == null }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            CoursesStatsCard(
                totalCourses = courses.size,
                completedCourses = courses.count { it.progress == 1f },
                inProgressCourses = courses.count { it.progress != null && it.progress in 0f..0.99f && it.progress > 0f },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (filteredByStatus.isEmpty()) {
            item {
                EmptyCoursesMessage(
                    filter = selectedFilter,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(
                items = filteredByStatus,
                key = { course -> course.id }
            ) { course ->
                ContinueLearningCard(
                    course = course,
                    onContinueClick = { onCourseClick(course.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(Res.string.search_courses)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun CoursesStatsCard(
    totalCourses: Int,
    completedCourses: Int,
    inProgressCourses: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = totalCourses.toString(),
                label = stringResource(Res.string.total),
                color = MaterialTheme.colorScheme.primary
            )
            StatItem(
                value = inProgressCourses.toString(),
                label = stringResource(Res.string.in_progress),
                color = MaterialTheme.colorScheme.secondary
            )
            StatItem(
                value = completedCourses.toString(),
                label = stringResource(Res.string.completed),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class Filter {
    ALL,
    IN_PROGRESS,
    COMPLETED,
    NOT_STARTED
}

@Composable
fun FilterChipsRow(
    selectedFilter: Filter,
    onFilterSelected: (Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        item {
            FilterChip(
                selected = selectedFilter == Filter.ALL,
                onClick = { onFilterSelected(Filter.ALL) },
                label = {
                    Text(
                        text = stringResource(Res.string.all_courses),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.wrapContentSize()
            )
        }
        item {
            FilterChip(
                selected = selectedFilter == Filter.IN_PROGRESS,
                onClick = { onFilterSelected(Filter.IN_PROGRESS) },
                label = {
                    Text(
                        text = stringResource(Res.string.in_progress),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.wrapContentSize()
            )
        }
        item {
            FilterChip(
                selected = selectedFilter == Filter.COMPLETED,
                onClick = { onFilterSelected(Filter.COMPLETED) },
                label = {
                    Text(
                        text = stringResource(Res.string.completed),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.wrapContentSize()
            )
        }
        item {
            FilterChip(
                selected = selectedFilter == Filter.NOT_STARTED,
                onClick = { onFilterSelected(Filter.NOT_STARTED) },
                label = {
                    Text(
                        text = stringResource(Res.string.not_started),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}

@Composable
fun EmptyCoursesMessage(
    filter: Filter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📭",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (filter) {
                    Filter.ALL -> stringResource(Res.string.no_courses_yet)
                    Filter.IN_PROGRESS -> stringResource(Res.string.no_courses_in_progress)
                    Filter.COMPLETED -> stringResource(Res.string.no_completed_courses)
                    Filter.NOT_STARTED -> stringResource(Res.string.no_courses_not_started)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
