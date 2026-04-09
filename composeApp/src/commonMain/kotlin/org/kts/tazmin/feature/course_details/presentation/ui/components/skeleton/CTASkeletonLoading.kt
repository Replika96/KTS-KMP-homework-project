package org.kts.tazmin.feature.course_details.presentation.ui.components.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun CTASkeletonLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(20.dp)
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // цена
            ShimmerPlaceholder(
                modifier = Modifier
                    .height(28.dp)
                    .width(120.dp)
            )

            // старая цена
            ShimmerPlaceholder(
                modifier = Modifier
                    .height(16.dp)
                    .width(80.dp)
            )

            // рейтинг
            ShimmerPlaceholder(
                modifier = Modifier
                    .height(20.dp)
                    .width(140.dp)
            )

            // прогресс
            ShimmerPlaceholder(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.6f)
            )

            // кнопка
            ShimmerPlaceholder(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
                shape = shape
            )
    )
}
