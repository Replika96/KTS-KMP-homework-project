package org.kts.tazmin.feature.course_details.data.local.fullEntity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.kts.tazmin.feature.course_details.data.local.CourseDetailsEntity
import org.kts.tazmin.feature.course_details.data.local.EnrollmentEntity
import org.kts.tazmin.feature.course_details.data.local.ProgressEntity
import org.kts.tazmin.feature.course_details.data.local.ReviewSummaryEntity
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseAuthorCrossRef
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseInstructorCrossRef
import org.kts.tazmin.feature.profile.data.local.UserEntity

data class CourseFull(

    @Embedded
    val course: CourseDetailsEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CourseAuthorCrossRef::class,
            parentColumn = "courseId",
            entityColumn = "userId"
        )
    )
    val authors: List<UserEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CourseInstructorCrossRef::class,
            parentColumn = "courseId",
            entityColumn = "userId"
        )
    )
    val instructors: List<UserEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )
    val review: ReviewSummaryEntity? = null,

    @Relation(
        parentColumn = "progressId",
        entityColumn = "id"
    )
    val progress: ProgressEntity? = null,

    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )
    val enrollment: EnrollmentEntity? = null
)
