package org.kts.tazmin.feature.course_details.presentation.ui.components.skeleton

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun InfoSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // summary
        InfoSectionSkeleton(lines = 2)

        // о курсе
        InfoSectionSkeleton(lines = 4)

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // требования
        InfoSectionSkeleton(lines = 3)

        // для кого курс
        InfoSectionSkeleton(lines = 3)

        // длительность
        InfoSectionSkeleton(lines = 1)

        // язык
        InfoSectionSkeleton(lines = 1)

        // сертификат
        InfoSectionSkeleton(lines = 2)

        // ученики
        InfoSectionSkeleton(lines = 1)

        // преподаватели
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(
                modifier = Modifier
                    .width(160.dp)
                    .height(20.dp)
            )

            repeat(2) {
                InstructorSkeleton()
            }
        }
    }
}

@Composable
private fun InstructorSkeleton() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ShimmerBox(
            modifier = Modifier
                .size(48.dp),
            shape = CircleShape
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(160.dp)
                    .height(16.dp)
            )

            ShimmerBox(
                modifier = Modifier
                    .width(200.dp)
                    .height(12.dp)
            )
        }
    }
}

@Composable
private fun InfoSectionSkeleton(
    lines: Int = 3
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // заголовок
        ShimmerBox(
            modifier = Modifier
                .width(140.dp)
                .height(20.dp)
        )

        repeat(lines) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
        }
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnim"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(shimmerBrush)
    )
}
