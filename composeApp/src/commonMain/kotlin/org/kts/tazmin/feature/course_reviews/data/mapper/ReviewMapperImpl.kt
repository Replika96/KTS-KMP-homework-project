package org.kts.tazmin.feature.course_reviews.data.mapper

import org.kts.tazmin.feature.course_reviews.data.local.ReviewEntity
import org.kts.tazmin.feature.course_reviews.data.response.CourseReviewDto
import org.kts.tazmin.feature.course_reviews.domain.mapper.ReviewMapper
import org.kts.tazmin.feature.course_reviews.domain.model.Review

class ReviewMapperImpl: ReviewMapper {

    override fun toDomain(entity: ReviewEntity): Review =
        Review(
            id = entity.id,
            courseId = entity.courseId,
            userId = entity.userId,
            score = entity.score,
            text = entity.text,
            replyText = entity.replyText,
            createDate = entity.createDate,
            updateDate = entity.updateDate,
            epicCount = entity.epicCount,
            abuseCount = entity.abuseCount,
            voteDelta = entity.voteDelta,
            vote = entity.vote,
            isPending = entity.isPending
        )

    override fun toEntity(dto: CourseReviewDto, courseId: Long, localOrder: Long): ReviewEntity =
        ReviewEntity(
            id = dto.id,
            courseId = courseId,
            userId = dto.user.toLongOrNull() ?: 0L,
            score = dto.score,
            text = dto.text,
            replyText = dto.replyText,
            createDate = dto.createDate,
            updateDate = dto.updateDate,
            epicCount = dto.epicCount,
            abuseCount = dto.abuseCount,
            voteDelta = dto.voteDelta,
            vote = dto.vote,
            localOrder = localOrder
        )
}
