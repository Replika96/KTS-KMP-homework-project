package org.kts.tazmin.feature.courses.data.mapper

import org.kts.tazmin.feature.courses.data.local.CourseEntity
import org.kts.tazmin.feature.courses.domain.entity.Course
import kotlin.time.Clock

object CourseDbMapper {

    fun Course.toEntity(
        page: Int,
        query: String? = null
    ): CourseEntity {
        return CourseEntity(
            id = id,
            title = title,
            description = description,
            author = author,
            coverUrl = coverUrl,
            rating = rating,
            studentsCount = studentsCount,
            isPaid = isPaid,
            price = price,
            cacheTimestamp = Clock.System.now().toEpochMilliseconds(),
            cachePage = page,
            cacheQuery = query
        )
    }

    fun CourseEntity.toDomain(): Course {
        return Course(
            id = id,
            title = title,
            description = description,
            author = author,
            coverUrl = coverUrl,
            rating = rating,
            studentsCount = studentsCount,
            isPaid = isPaid,
            price = price
        )
    }

    fun List<CourseEntity>.toDomain(): List<Course> = map { it.toDomain() }
}
