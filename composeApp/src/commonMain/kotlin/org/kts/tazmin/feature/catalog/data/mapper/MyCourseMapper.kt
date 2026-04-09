package org.kts.tazmin.feature.catalog.data.mapper

import org.kts.tazmin.feature.catalog.data.local.MyCourseEntity
import org.kts.tazmin.feature.catalog.data.model.CourseDto
import org.kts.tazmin.feature.catalog.data.model.ProgressDto
import org.kts.tazmin.feature.catalog.domain.entity.Course
import org.kts.tazmin.feature.catalog.domain.mapper.MyCourseMapper
import kotlin.time.Clock

class MyCourseMapperImpl: MyCourseMapper {

    override fun toDomain(dto: CourseDto, progressDto: ProgressDto?): Course {
        return Course(
            id = dto.id,
            title = dto.title,
            description = dto.summary ?: "",
            author = "Stepik",
            coverUrl = dto.cover,
            rating = 0.0,
            studentsCount = dto.learnersCount ?: 0,
            isPaid = dto.isPaid ?: false,
            price = dto.displayPrice,
            progress = progressDto?.let {
                if (it.cost == 0) 0f else it.score.toFloat() / it.cost.toFloat()
            },
            score = progressDto?.score?.toInt(),
            cost = progressDto?.cost
        )
    }

    override fun toEntity(course: Course): MyCourseEntity {
        return MyCourseEntity(
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

            cacheTimestamp = Clock.System.now().toEpochMilliseconds()
        )
    }


    override fun toDomain(entity: MyCourseEntity): Course {
        return Course(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            author = entity.author,
            coverUrl = entity.coverUrl,
            rating = entity.rating,
            studentsCount = entity.studentsCount,
            isPaid = entity.isPaid,
            price = entity.price,
            progress = entity.progress,
            score = entity.score,
            cost = entity.cost
        )
    }
}
