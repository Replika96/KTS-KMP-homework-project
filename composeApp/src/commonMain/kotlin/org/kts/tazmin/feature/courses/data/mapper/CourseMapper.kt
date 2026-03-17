package org.kts.tazmin.feature.courses.data.mapper

import org.kts.tazmin.feature.courses.data.model.CourseDto
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.courses.domain.entity.Course

object CourseMapper {
    fun mapToDomain(dto: CourseDto, reviewSummary: ReviewSummaryDto? = null): Course {
        return Course(
            id = dto.id,
            title = dto.title,
            description = dto.summary ?: "Нет описания",
            author = "Stepik",
            coverUrl = dto.cover,
            rating = reviewSummary?.average ?: 0.0,
            studentsCount = dto.learnersCount ?: 0,
            isPaid = dto.isPaid ?: false,
            price = dto.displayPrice
        )
    }

    fun mapToDomainList(dtos: List<CourseDto>, reviewSummaries: Map<Int, ReviewSummaryDto> = emptyMap()): List<Course> {
        return dtos.map { dto ->
            mapToDomain(dto, reviewSummaries[dto.reviewSummary ?: -1])
        }
    }
}
