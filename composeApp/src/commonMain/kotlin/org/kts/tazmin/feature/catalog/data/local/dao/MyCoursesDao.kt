package org.kts.tazmin.feature.catalog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.catalog.data.local.MyCourseEntity

@Dao
interface MyCoursesDao {
    @Query("SELECT * FROM my_courses")
    fun observeMyCourses(): Flow<List<MyCourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyCourses(courses: List<MyCourseEntity>)

    @Query("DELETE FROM my_courses")
    suspend fun clear()
}
