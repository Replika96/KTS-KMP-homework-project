package org.kts.tazmin.feature.course_details.data.mapper

import org.kts.tazmin.feature.course_details.data.local.CourseDetailsEntity
import org.kts.tazmin.feature.course_details.data.local.EnrollmentEntity
import org.kts.tazmin.feature.course_details.data.local.ProgressEntity
import org.kts.tazmin.feature.course_details.data.local.ReviewSummaryEntity
import org.kts.tazmin.feature.course_details.domain.entity.CourseCTA
import org.kts.tazmin.feature.course_details.domain.entity.Progress

class CourseCTAMapper {
    fun mapToDomain(
        course: CourseDetailsEntity,
        enrollment: EnrollmentEntity?,
        progress: ProgressEntity?,
        review: ReviewSummaryEntity?
    ) = CourseCTA(
        isEnrolled = !course.progressId.isNullOrEmpty() ||
                progress != null || enrollment?.isActive == true,
        isPaid = course.isPaid,
        price = course.displayPrice,
        discountPrice = course.discountPrice,
        discountUntil = course.discountDeadline,
        progress = progress?.let {
            Progress(
                steps = it.steps,
                stepsPassed = it.stepsPassed,
                isPassed = it.isPassed
            )
        },
        rating = review?.average ?: 0.0,
        reviewsCount = review?.count ?: 0
    )
}
