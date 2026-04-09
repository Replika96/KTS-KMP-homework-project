package org.kts.tazmin.feature.course_reviews.domain.mapper

import org.kts.tazmin.feature.course_reviews.data.local.ReviewEntity
import org.kts.tazmin.feature.course_reviews.data.response.CourseReviewDto
import org.kts.tazmin.feature.course_reviews.domain.model.Review

interface ReviewMapper {

    fun toDomain(entity: ReviewEntity): Review

    fun toEntity(dto: CourseReviewDto, courseId: Long, localOrder: Long = 0L): ReviewEntity
}
