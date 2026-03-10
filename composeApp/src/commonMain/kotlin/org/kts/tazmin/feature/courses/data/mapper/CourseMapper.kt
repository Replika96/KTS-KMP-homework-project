package org.kts.tazmin.feature.courses.data.mapper

import org.kts.tazmin.feature.courses.data.model.CourseDto
import org.kts.tazmin.feature.courses.domain.entity.Course

object CourseMapper {

    fun mapToDomain(dto: CourseDto): Course {
        return Course(
            id = dto.id,
            title = dto.title,
            description = dto.summary ?: "Нет описания",
            author = "Stepik",
            coverUrl = dto.cover,
            rating = dto.averageRating ?: 0.0,
            studentsCount = dto.learnersCount ?: 0
        )
    }

    fun mapToDomainList(dtos: List<CourseDto>): List<Course> {
        return dtos.map { mapToDomain(it) }
    }
}
