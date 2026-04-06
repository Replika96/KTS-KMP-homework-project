package org.kts.tazmin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.kts.tazmin.feature.courses.data.local.CourseDao
import org.kts.tazmin.feature.courses.data.local.CourseEntity
import org.kts.tazmin.feature.profile.data.local.UserDao
import org.kts.tazmin.feature.profile.data.local.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
}
