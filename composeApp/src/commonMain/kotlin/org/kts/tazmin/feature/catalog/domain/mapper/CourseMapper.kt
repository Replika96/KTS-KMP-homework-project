package org.kts.tazmin.feature.catalog.domain.mapper

import org.kts.tazmin.feature.catalog.data.local.CourseEntity
import org.kts.tazmin.feature.catalog.data.local.CourseSource
import org.kts.tazmin.feature.catalog.data.model.CourseDto
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.catalog.domain.entity.Course

interface CourseMapper {

    fun fromDto(dto: CourseDto, reviewSummary: ReviewSummaryDto?): Course

    fun fromDtoList(
        dtos: List<CourseDto>,
        reviewSummaries: Map<Int, ReviewSummaryDto>
    ): List<Course>

    fun toEntity(
        course: Course,
        page: Int?,
        query: String?,
        source: CourseSource?
    ): CourseEntity

    fun fromEntity(entity: CourseEntity): Course
}
