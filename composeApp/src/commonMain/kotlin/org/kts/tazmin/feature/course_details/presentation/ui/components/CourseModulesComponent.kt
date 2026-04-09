package org.kts.tazmin.feature.course_details.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.collapse
import ktskotlinproject.composeapp.generated.resources.expand
import ktskotlinproject.composeapp.generated.resources.go_to_lesson
import org.jetbrains.compose.resources.stringResource
import org.kts.tazmin.feature.course_details.domain.entity.CourseLesson
import org.kts.tazmin.feature.course_details.domain.entity.CourseModule


@Composable
fun CourseModulesComponent(
    modules: List<CourseModule>,
    onLessonClick: (lessonId: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        modules.forEach { module ->
            ModuleCard(
                module = module,
                onLessonClick = onLessonClick
            )
        }
    }
}

@Composable
private fun ModuleCard(
    module: CourseModule,
    onLessonClick: (lessonId: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {

        // заголовок модуля
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded)
                    stringResource(Res.string.collapse)
                else
                    stringResource(Res.string.expand)
            )
        }

        Spacer(Modifier.height(8.dp))

        // прогресс модуля
        LinearProgressIndicator(
            progress = { module.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                module.lessons.forEachIndexed { index, lesson ->
                    LessonRow(
                        index = index + 1,
                        lesson = lesson,
                        onClick = { onLessonClick(lesson.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonRow(
    index: Int,
    lesson: CourseLesson,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // номер урока
        Text(
            text = index.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(28.dp)
        )

        // название урока
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            stringResource(Res.string.go_to_lesson)
        )
    }
}
