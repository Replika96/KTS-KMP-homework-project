package org.kts.tazmin.feature.course_details.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.back
import ktskotlinproject.composeapp.generated.resources.favorite
import ktskotlinproject.composeapp.generated.resources.share
import org.jetbrains.compose.resources.stringResource
import org.kts.tazmin.feature.course_details.domain.entity.CourseHeader


@Composable
fun CourseHeaderComponent(
    header: CourseHeader,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back)
            )
        }
        Text(
            text = header.title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = onToggleFavorite) {
            val isFav = header.isFavorite
            Icon(
                imageVector = if (isFav) Icons.Default.Favorite
                else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(Res.string.favorite)
            )
        }
        IconButton(onClick = onShare) {
            Icon(
                Icons.Default.Share,
                contentDescription = stringResource(Res.string.share)
            )
        }
    }
}

@Composable
fun TitleSkeleton(
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = stringResource(Res.string.favorite)
            )
        }
        IconButton(onClick = { }) {
            Icon(
                Icons.Default.Share,
                contentDescription = stringResource(Res.string.share)
            )
        }
    }
}
