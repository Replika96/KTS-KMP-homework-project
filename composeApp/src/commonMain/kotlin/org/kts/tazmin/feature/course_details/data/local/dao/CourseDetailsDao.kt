package org.kts.tazmin.feature.course_details.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.data.local.CourseDetailsEntity
import org.kts.tazmin.feature.course_details.data.local.EnrollmentEntity
import org.kts.tazmin.feature.course_details.data.local.ProgressEntity
import org.kts.tazmin.feature.course_details.data.local.ReviewSummaryEntity
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseAuthorCrossRef
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseInstructorCrossRef
import org.kts.tazmin.feature.course_details.data.local.fullEntity.CourseFull
import org.kts.tazmin.feature.profile.data.local.UserEntity

@Dao
interface CourseDetailsDao {

    @Transaction
    @Query("SELECT * FROM course WHERE id = :id")
    fun observeCourse(id: Int): Flow<CourseFull?>

    @Query("SELECT * FROM course WHERE id = :id")
    suspend fun getCourse(id: Int): CourseDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(refs: List<CourseAuthorCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructors(refs: List<CourseInstructorCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewSummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollmnet: EnrollmentEntity)

    @Transaction
    suspend fun insertCourseBundle(
        course: CourseDetailsEntity,
        users: List<UserEntity>,
        authors: List<CourseAuthorCrossRef>,
        instructors: List<CourseInstructorCrossRef>,
        review: ReviewSummaryEntity?,
        progress: ProgressEntity?,
        enrollment: EnrollmentEntity?
    ) {
        insertCourse(course)
        insertUsers(users)
        insertAuthors(authors)
        insertInstructors(instructors)

        review?.let { insertReview(it) }
        progress?.let { insertProgress(it) }
        enrollment?.let { insertEnrollment(it) }
    }
}
