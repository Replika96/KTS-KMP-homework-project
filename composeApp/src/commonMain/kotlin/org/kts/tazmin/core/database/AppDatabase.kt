package org.kts.tazmin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.kts.tazmin.feature.courses.data.local.CatalogDao
import org.kts.tazmin.feature.courses.data.local.CatalogSectionEntity
import org.kts.tazmin.feature.courses.data.local.CatalogSectionItemEntity
import org.kts.tazmin.feature.courses.data.local.CourseDao
import org.kts.tazmin.feature.courses.data.local.CourseEntity
import org.kts.tazmin.feature.courses.data.local.MyCourseEntity
import org.kts.tazmin.feature.courses.data.local.MyCoursesDao
import org.kts.tazmin.feature.profile.data.local.UserDao
import org.kts.tazmin.feature.profile.data.local.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        MyCourseEntity::class,
        CatalogSectionEntity::class,
        CatalogSectionItemEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun myCoursesDao(): MyCoursesDao
    abstract fun catalogDao(): CatalogDao
}
