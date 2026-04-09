package org.kts.tazmin.feature.course_details.data.mapper

import org.kts.tazmin.feature.course_details.data.local.CourseDetailsEntity
import org.kts.tazmin.feature.course_details.domain.entity.CourseHeader

object CourseHeaderMapper {
    fun mapToDomain(entity: CourseDetailsEntity) = CourseHeader(
        title = entity.title,
        isFavorite = entity.isFavorite,
        shareUrl = entity.shareUrl
    )
}

