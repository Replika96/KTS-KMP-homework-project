package org.kts.tazmin.feature.courses.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses WHERE cacheQuery IS NULL AND cachePage = :page")
    suspend fun getCoursesByPage(page: Int): List<CourseEntity>

    @Query("""
    SELECT * FROM courses 
    WHERE cacheQuery IS NULL AND cachePage = :page
    """)
    fun observeCoursesByPage(page: Int): Flow<List<CourseEntity>>
    @Query("SELECT * FROM courses WHERE cacheQuery = :query AND cachePage = :page")
    suspend fun getSearchResults(query: String, page: Int): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Int): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("""
    SELECT * FROM courses 
    WHERE cacheQuery = :query AND cachePage = :page
    """)
    fun observeSearchResults(query: String, page: Int): Flow<List<CourseEntity>>

    @Query("DELETE FROM courses WHERE cacheQuery IS NULL AND cachePage = :page")
    suspend fun clearPage(page: Int)

    @Query("DELETE FROM courses WHERE cacheQuery = :query")
    suspend fun clearSearch(query: String)

    @Query("SELECT MAX(cachePage) FROM courses WHERE cacheQuery IS NULL")
    suspend fun getMaxCachedPage(): Int?
}
