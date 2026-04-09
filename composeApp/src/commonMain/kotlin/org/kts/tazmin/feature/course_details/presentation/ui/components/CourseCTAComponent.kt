package org.kts.tazmin.feature.course_details.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.continue_learning
import ktskotlinproject.composeapp.generated.resources.discount_until
import ktskotlinproject.composeapp.generated.resources.enroll_course
import ktskotlinproject.composeapp.generated.resources.your_progress
import org.jetbrains.compose.resources.stringResource
import org.kts.tazmin.feature.course_details.domain.entity.CourseCTA
import org.kts.tazmin.feature.course_details.domain.entity.Progress
import kotlin.time.Instant

@Composable
fun CourseCTAComponent(
    cta: CourseCTA,
    onEnrollClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    enrolledCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Кнопка всегда первая
        CTAButton(
            isEnrolled = cta.isEnrolled,
            isPaid = cta.isPaid,
            price = cta.discountPrice ?: cta.price,
            oldPrice = cta.discountPrice?.let { cta.price },
            onEnrollClick = onEnrollClick,
            onContinueClick = onContinueClick
        )

        // Прогресс — только если записан
        if (cta.isEnrolled && cta.progress != null) {

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            ProgressInline(progress = cta.progress)
        }

        // Дата скидки
        if (!cta.isEnrolled && cta.discountPrice != null && cta.discountUntil != null) {
            Text(
                text = "${stringResource(Res.string.discount_until)} ${formatDiscountDate(cta.discountUntil)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Количество записавшихся
        if (!cta.isEnrolled) {
            EnrolledCount(enrolledCount)
        }
    }
}


@Composable
private fun EnrolledCount(enrolledCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = enrolledCount.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.width(6.dp))

        Icon(
            imageVector = Icons.Default.People,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )

    }
}

@Composable
fun ProgressInline(progress: Progress) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.your_progress),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "${progress.percent}%",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(6.dp))

        SegmentedProgress(
            steps = progress.steps,
            passed = progress.stepsPassed
        )
    }
}


@Composable
fun SegmentedProgress(steps: Int, passed: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(steps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (index < passed)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
private fun CTAButton(
    isEnrolled: Boolean,
    isPaid: Boolean,
    price: String?,
    oldPrice: String?,
    onEnrollClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Button(
        onClick = { if (isEnrolled) onContinueClick() else onEnrollClick() },
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        if (isEnrolled) {
            Text(stringResource(Res.string.continue_learning))
        } else if (isPaid) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // новая цена
                Text(
                    text = formatPrice(price ?: ""),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )

                // cтарая зачёркнутая
                if (oldPrice != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = oldPrice,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        } else {
            Text(stringResource(Res.string.enroll_course))
        }
    }
}


fun formatPrice(price: String): String {
    val value = price.toDoubleOrNull() ?: return price

    val whole = value.toInt()
    val formatted = whole
        .toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()

    return "$formatted ₽"
}

fun formatDiscountDate(raw: String?): String? {
    if (raw == null) return null

    return try {
        val instant = Instant.parse(raw)
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val months = listOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        )

        "${local.day} ${months[local.month.number - 1]}"
    } catch (_: Exception) {
        raw
    }
}
