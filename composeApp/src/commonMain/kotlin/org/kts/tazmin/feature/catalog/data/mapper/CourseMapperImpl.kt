package org.kts.tazmin.feature.catalog.data.mapper

import org.kts.tazmin.feature.catalog.data.local.CourseEntity
import org.kts.tazmin.feature.catalog.data.model.CourseDto
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.catalog.domain.entity.Course
import org.kts.tazmin.feature.catalog.data.local.CourseSource
import org.kts.tazmin.feature.catalog.domain.mapper.CourseMapper

class CourseMapperImpl: CourseMapper {

    // DTO -> Domain
    override fun fromDto(dto: CourseDto, reviewSummary: ReviewSummaryDto?): Course {
        return Course(
            id = dto.id,
            title = dto.title,
            description = dto.summary ?: "Нет описания",
            author = "Stepik",
            coverUrl = dto.cover,
            rating = reviewSummary?.average ?: 0.0,
            studentsCount = dto.learnersCount ?: 0,
            isPaid = dto.isPaid ?: false,
            price = dto.displayPrice,
            progress = null,
            score = null,
            cost = null
        )
    }

    override fun fromDtoList(
        dtos: List<CourseDto>,
        reviewSummaries: Map<Int, ReviewSummaryDto>
    ): List<Course> {
        return dtos.map { dto ->
            val summary = reviewSummaries[dto.id]
            fromDto(dto, summary)
        }
    }

    override fun toEntity(
        course: Course,
        page: Int?,
        query: String?,
        source: CourseSource?
    ): CourseEntity {
        return CourseEntity(
            id = course.id,
            title = course.title,
            description = course.description,
            author = course.author,
            coverUrl = course.coverUrl,
            rating = course.rating,
            studentsCount = course.studentsCount,
            isPaid = course.isPaid,
            price = course.price,
            progress = course.progress,
            score = course.score,
            cost = course.cost,
            cachePage = page,
            cacheQuery = query,
            source = source
        )
    }

    override fun fromEntity(entity: CourseEntity): Course {
        return Course(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            author = entity.author,
            coverUrl = entity.coverUrl,
            rating = entity.rating ?: 0.0,
            studentsCount = entity.studentsCount,
            isPaid = entity.isPaid,
            price = entity.price,
            progress = entity.progress,
            score = entity.score,
            cost = entity.cost
        )
    }
}
