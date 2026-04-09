package org.kts.tazmin.feature.course_reviews.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.kts.tazmin.feature.course_reviews.domain.model.RatingSummary
import org.kts.tazmin.feature.course_reviews.domain.model.Review
import org.kts.tazmin.feature.course_reviews.presentation.state.ReviewStateHolder
import org.kts.tazmin.feature.course_reviews.presentation.state.ReviewUiState
import org.kts.tazmin.feature.course_reviews.presentation.viewmodel.ReviewViewModel
import kotlin.math.round

@Composable
fun rememberReviewState(courseId: Int): ReviewStateHolder {
    val viewModel: ReviewViewModel = koinViewModel(
        parameters = { parametersOf(courseId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    val summary = remember {
        derivedStateOf {
            val total = state.reviews.size
            val counts = (1..5).associateWith { star ->
                state.reviews.count { it.score == star }
            }
            val average = if (total == 0) 0f
            else state.reviews.sumOf { it.score }.toFloat() / total
            RatingSummary(average, total, counts)
        }
    }.value

    return ReviewStateHolder(state, summary, viewModel)
}

fun LazyListScope.reviewsContent(
    state: ReviewUiState,
    summary: RatingSummary,
    onVote: (Long, String) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit,
    onFilterSelected: (Int?) -> Unit
) {
    item {
        RatingSummaryBlock(
            summary = summary,
            selected = state.selectedScore,
            onFilterSelected = onFilterSelected
        )
    }
    item {
        ReviewFilter(
            selected = state.selectedScore,
            onSelected = onFilterSelected
        )
    }
    when {
        state.reviews.isEmpty() && state.isRefreshing -> {
            item { ReviewsLoading() }
        }

        state.reviews.isEmpty() && state.error != null -> {
            item { ReviewsError(message = state.error, onRetry = onRetry) }
        }

        else -> {
            items(items = state.reviews, key = { it.id }) { review ->
                ReviewCard(review = review, onVote = onVote)
            }
            if (state.isLoadingMore) {
                item { ReviewsLoading() }
            }
            if (state.hasMore && !state.isLoadingMore && state.reviews.isNotEmpty()) {
                item {
                    LaunchedEffect(state.hasMore, state.isLoadingMore) {
                        if (state.hasMore && !state.isLoadingMore) {
                            onLoadNextPage()
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun RatingSummaryBlock(
    summary: RatingSummary,
    onFilterSelected: (Int?) -> Unit,
    selected: Int?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.width(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatRating(summary.average),
                style = MaterialTheme.typography.headlineMedium
            )

            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (index < summary.average.toInt())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = "${summary.total} reviews",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (5 downTo 1).forEach { star ->
                RatingBarRow(
                    star = star,
                    count = summary.counts[star] ?: 0,
                    total = summary.total,
                    isSelected = selected == star,
                    onClick = { onFilterSelected(star) }
                )
            }
        }
    }
}

@Composable
fun RatingBarRow(
    star: Int,
    count: Int,
    total: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val progress = if (total == 0) 0f else count.toFloat() / total

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        Text(
            text = "$star",
            modifier = Modifier.width(20.dp),
            style = MaterialTheme.typography.bodySmall
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(32.dp)
        )
    }
}

@Composable
private fun ReviewFilter(
    selected: Int?,
    onSelected: (Int?) -> Unit
) {
    val options = listOf(null, 5, 4, 3, 2, 1)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { score ->

            val isSelected = score == selected

            FilterChip(
                selected = isSelected,
                onClick = { onSelected(score) },
                label = {
                    if (score == null) {
                        Text("All")
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$score")
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onVote: (Long, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // рейтинг
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < review.score)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // дата
                Text(
                    text = review.createDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.height(10.dp))

            // текст отзыва
            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            review.replyText?.takeIf { it.isNotBlank() }?.let { reply ->
                Spacer(Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Ответ преподавателя",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = reply,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // голосование
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                VoteButton(
                    selected = review.vote == "up",
                    icon = Icons.Default.KeyboardArrowUp,
                    onClick = { onVote(review.id, "up") }
                )

                AnimatedContent(
                    targetState = review.voteDelta,
                    label = "voteDeltaAnim"
                ) { delta ->
                    Text(
                        text = delta.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                VoteButton(
                    selected = review.vote == "down",
                    icon = Icons.Default.KeyboardArrowDown,
                    onClick = { onVote(review.id, "down") }
                )

                Spacer(Modifier.weight(1f))

                if (review.isPending) {
                    Text(
                        text = "Отправляется…",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}


fun formatRating(rating: Float): String {
    val rounded = round(rating * 10) / 10
    return rounded.toString().let {
        if (it.contains(".")) it else "$it.0"
    }
}

@Composable
private fun VoteButton(
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    Color.Transparent
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ReviewsLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ReviewsError(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
