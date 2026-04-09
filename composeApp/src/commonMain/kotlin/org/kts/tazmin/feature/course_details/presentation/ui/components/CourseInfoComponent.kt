package org.kts.tazmin.feature.course_details.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.course_about
import ktskotlinproject.composeapp.generated.resources.course_certificate
import ktskotlinproject.composeapp.generated.resources.course_certificate_available
import ktskotlinproject.composeapp.generated.resources.course_duration
import ktskotlinproject.composeapp.generated.resources.course_instructors
import ktskotlinproject.composeapp.generated.resources.course_language
import ktskotlinproject.composeapp.generated.resources.course_requirements
import ktskotlinproject.composeapp.generated.resources.course_students_count
import ktskotlinproject.composeapp.generated.resources.course_target_audience
import org.jetbrains.compose.resources.stringResource
import org.kts.tazmin.feature.course_details.domain.entity.CourseInfo
import org.kts.tazmin.feature.course_details.presentation.parser.HtmlText
import org.kts.tazmin.feature.course_details.presentation.parser.normalizeHtml
import org.kts.tazmin.feature.profile.domain.model.User

@Composable
fun CourseInfoComponent(info: CourseInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // краткое содержание
        info.summary?.let {
            HtmlText(it)
        }

        // описание курса
        InfoSection(
            title = stringResource(Res.string.course_about),
            html = info.description
        )

        // ───────────────────────────────────────────────
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // требования
        InfoRow(
            icon = Icons.Default.CheckCircle,
            title = stringResource(Res.string.course_requirements),
            html = normalizeHtml(info.requirements)
        )

        // для кого курс
        if (info.targetAudience.isNotEmpty()) {
            InfoRow(
                icon = Icons.Default.Group,
                title = stringResource(Res.string.course_target_audience),
                html = normalizeHtml(info.targetAudience)
            )
        }

        // длительность
        info.duration?.let {
            InfoRow(
                icon = Icons.Default.Schedule,
                title = stringResource(Res.string.course_duration),
                html = it
            )
        }

        // язык
        info.language?.let {
            InfoRow(
                icon = Icons.Default.Language,
                title =  stringResource(Res.string.course_language),
                html = it
            )
        }

        // сертификат
        if (info.certificateAvailable) {
            InfoRow(
                icon = Icons.Default.Verified,
                title = stringResource(Res.string.course_certificate),
                html = info.certificateDescription
                    ?: stringResource(Res.string.course_certificate_available)
            )
        }

        // количество учеников
        InfoRow(
            icon = Icons.Default.School,
            title = stringResource(Res.string.course_students_count),
            html = info.learnersCount.toString()
        )

        if (info.instructors.isNotEmpty()) {
            InstructorsSection(info.instructors)
        }

    }
}

@Composable
private fun InfoSection(
    title: String,
    html: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        HtmlText(html)
    }
}

@Composable
fun InstructorsSection(instructors: List<User>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(Res.string.course_instructors),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            instructors.forEach { user ->
                UserRow(user)
            }
        }
    }
}


@Composable
private fun UserRow(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column {
            Text(
                text = user.name.full,
                style = MaterialTheme.typography.bodyLarge
            )
            user.bio?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    title: String,
    html: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .paddingFromBaseline(top = 0.dp)
                .offset(y = 2.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            HtmlText(html)
        }
    }
}
