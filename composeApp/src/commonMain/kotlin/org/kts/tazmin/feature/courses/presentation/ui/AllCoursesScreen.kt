package org.kts.tazmin.feature.courses.presentation.ui

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
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
fun AllCoursesScreen(
    viewModel: CoursesViewModel = koinInject(),
    onCourseClick: (Int) -> Unit = {}
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.all_courses))
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Search
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.handleEvent(
                        CoursesUiEvent.SearchQueryChanged(it)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(Res.string.search_courses)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, null)
                },
                singleLine = true
            )

            val coursesToShow =
                if (state.searchQuery.isBlank())
                    state.courses
                else
                    state.searchResults

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {

                itemsIndexed(
                    items = coursesToShow,
                    key = { _, course -> course.id }
                ) { index, course ->

                    CourseCatalogItem(
                        course = course,
                        onClick = { onCourseClick(course.id) }
                    )

                    // Pagination
                    if (state.searchQuery.isBlank() &&
                        index >= coursesToShow.lastIndex - 1
                    ) {
                        LaunchedEffect(index) {
                            viewModel.handleEvent(
                                CoursesUiEvent.LoadMoreCourses
                            )
                        }
                    }
                }

                // Pagination loader
                if (state.isLoadingMore) {
                    item {
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

                // Search loader
                if (state.isSearching) {
                    item {
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
