package org.kts.tazmin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.kts.tazmin.feature.course_details.data.local.CourseDetailsEntity
import org.kts.tazmin.feature.course_details.data.local.EnrollmentEntity
import org.kts.tazmin.feature.course_details.data.local.LessonEntity
import org.kts.tazmin.feature.course_details.data.local.ProgressEntity
import org.kts.tazmin.feature.course_details.data.local.ReviewSummaryEntity
import org.kts.tazmin.feature.course_details.data.local.SectionEntity
import org.kts.tazmin.feature.course_details.data.local.StepEntity
import org.kts.tazmin.feature.course_details.data.local.UnitEntity
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseAuthorCrossRef
import org.kts.tazmin.feature.course_details.data.local.crossRef.CourseInstructorCrossRef
import org.kts.tazmin.feature.course_details.data.local.crossRef.LessonStepCrossRef
import org.kts.tazmin.feature.course_details.data.local.crossRef.SectionUnitCrossRef
import org.kts.tazmin.feature.course_details.data.local.dao.CourseDetailsDao
import org.kts.tazmin.feature.course_details.data.local.dao.CourseStructureDao
import org.kts.tazmin.feature.course_details.data.local.dao.EnrollmentDao
import org.kts.tazmin.feature.course_details.data.local.dao.ProgressDao
import org.kts.tazmin.feature.course_details.data.local.dao.ReviewSummaryDao
import org.kts.tazmin.feature.course_reviews.data.local.PendingActionEntity
import org.kts.tazmin.feature.course_reviews.data.local.ReviewEntity
import org.kts.tazmin.feature.course_reviews.data.local.ReviewQueryCacheEntity
import org.kts.tazmin.feature.course_reviews.data.local.dao.ReviewDao
import org.kts.tazmin.feature.course_reviews.data.local.dao.ReviewQueryDao
import org.kts.tazmin.feature.catalog.data.local.dao.CatalogDao
import org.kts.tazmin.feature.catalog.data.local.CatalogSectionEntity
import org.kts.tazmin.feature.catalog.data.local.CatalogSectionItemEntity
import org.kts.tazmin.feature.catalog.data.local.dao.CourseDao
import org.kts.tazmin.feature.catalog.data.local.CourseEntity
import org.kts.tazmin.feature.catalog.data.local.CourseListPageEntity
import org.kts.tazmin.feature.catalog.data.local.MyCourseEntity
import org.kts.tazmin.feature.catalog.data.local.dao.CourseListDao
import org.kts.tazmin.feature.catalog.data.local.dao.MyCoursesDao
import org.kts.tazmin.feature.catalog.data.local.ref.CourseListCourseRef
import org.kts.tazmin.feature.profile.data.local.ProfileDao
import org.kts.tazmin.feature.profile.data.local.ProfileEntity
import org.kts.tazmin.feature.profile.data.local.UserDao
import org.kts.tazmin.feature.profile.data.local.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        CourseEntity::class,
        MyCourseEntity::class,
        CatalogSectionEntity::class,
        CatalogSectionItemEntity::class,
        CourseListPageEntity::class,
        CourseListCourseRef::class,

        // для CourseDetails
        CourseDetailsEntity::class,
        EnrollmentEntity::class,
        ProgressEntity::class,
        ReviewSummaryEntity::class,

        // Для уроков и структуры курса
        LessonEntity::class,
        SectionEntity::class,
        StepEntity::class,
        UnitEntity::class,

        // Cross-reference таблицы
        CourseAuthorCrossRef::class,
        CourseInstructorCrossRef::class,
        LessonStepCrossRef::class,
        SectionUnitCrossRef::class,
        // отзывы
        ReviewEntity::class,
        ReviewQueryCacheEntity::class,
        PendingActionEntity::class
    ],
    version = 14,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun courseDao(): CourseDao
    abstract fun myCoursesDao(): MyCoursesDao
    abstract fun catalogDao(): CatalogDao
    abstract fun courseListDao(): CourseListDao
    // DAO для CourseDetails
    abstract fun courseDetailsDao(): CourseDetailsDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun progressDao(): ProgressDao
    abstract fun reviewSummaryDao(): ReviewSummaryDao

    // DAO для уроков и структуры
    abstract fun courseStructureDao(): CourseStructureDao

    abstract fun reviewDao(): ReviewDao

    abstract fun reviewQueryDao(): ReviewQueryDao
}
