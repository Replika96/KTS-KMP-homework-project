package org.kts.tazmin.feature.course_details.data.mapper

import org.kts.tazmin.feature.catalog.data.model.ProgressDto
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.course_details.data.local.CourseDetailsEntity
import org.kts.tazmin.feature.course_details.data.local.EnrollmentEntity
import org.kts.tazmin.feature.course_details.data.local.ProgressEntity
import org.kts.tazmin.feature.course_details.data.local.ReviewSummaryEntity
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseAuthorCrossRef
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseInstructorCrossRef
import org.kts.tazmin.feature.course_details.data.response.CourseDetailsDto
import org.kts.tazmin.feature.course_details.data.response.EnrollmentDto
import org.kts.tazmin.feature.profile.data.local.UserEntity
import org.kts.tazmin.feature.profile.data.model.UserDto
import kotlin.time.Clock

fun CourseDetailsDto.toEntity() = CourseDetailsEntity(
    id = id,
    title = title,
    summary = summary,
    description = description,
    coverUrl = cover,
    workload = workload,
    isEnrolled = !progressId.isNullOrEmpty(),
    learnersCount = learnersCount ?: 0,
    displayPrice = displayPrice,
    isPaid = isPaid ?: false,
    discountPrice = discountPrice,
    discountDeadline = discountDeadline,
    isVerified = isVerified ?: false,
    certificateAvailable = certificateAvailable ?: false,
    certificateDescription = certificateDescription,
    language = language,
    reviewSummaryId = reviewSummaryId,
    progressId = progressId,
    isFavorite = actions?.isFavorite ?: false,
    shareUrl = actions?.shareUrl,
    requirements = requirements,
    targetAudience = targetAudience,
    lastUpdated = Clock.System.now().toEpochMilliseconds(),
    sectionIds = sectionIds?.joinToString(",") ?: ""
)

fun ProgressDto.toEntity() = ProgressEntity(
    id = id,
    steps = steps,
    stepsPassed = stepsPassed,
    isPassed = isPassed,
    score = score,
    cost = cost
)

fun EnrollmentDto.toEntity() = EnrollmentEntity(
    id = id,
    courseId = courseId,
    isActive = isActive
)

fun CourseDetailsDto.toAuthorRefs(): List<CourseAuthorCrossRef> =
    authors?.map { authorId ->
        CourseAuthorCrossRef(courseId = id, userId = authorId)
    } ?: emptyList()

fun CourseDetailsDto.toInstructorRefs(): List<CourseInstructorCrossRef> =
    instructorIds?.map { instructorId ->
        CourseInstructorCrossRef(courseId = id, userId = instructorId)
    } ?: emptyList()

fun UserDto.toEntity() = UserEntity(
    id = id,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    avatarUrl = avatar,
    bio = shortBio ?: details,
    knowledge = knowledge,
    knowledgeRank = knowledgeRank ?: 0,
    reputation = reputation,
    reputationRank = reputationRank ?: 0,
    followers = followersCount,
    solvedSteps = solvedStepsCount ?: 0,
    joinedAt = joinDate ?: "",
    isPrivate = isPrivate
)

fun ReviewSummaryDto.toEntity(courseId: Int) = ReviewSummaryEntity(
    courseId = courseId,
    average = average ?: 0.0,
    count = count ?: 0
)
