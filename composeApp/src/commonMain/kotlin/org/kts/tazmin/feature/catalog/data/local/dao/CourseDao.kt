package org.kts.tazmin.feature.catalog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.kts.tazmin.feature.catalog.data.local.CourseEntity

@Dao
interface CourseDao {

    //пагинация
    @Query("SELECT * FROM courses WHERE cacheQuery IS NULL AND cachePage = :page")
    suspend fun getCoursesByPage(page: Int): List<CourseEntity>

    @Query("SELECT MAX(cachePage) FROM courses WHERE cacheQuery IS NULL")
    suspend fun getMaxCachedPage(): Int?

    @Query("DELETE FROM courses WHERE cacheQuery IS NULL AND cachePage = :page")
    suspend fun clearPage(page: Int)


    //поиск
    @Query("SELECT * FROM courses WHERE cacheQuery = :query AND cachePage = :page")
    suspend fun getSearchResults(query: String, page: Int): List<CourseEntity>

    @Query("DELETE FROM courses WHERE cacheQuery = :query")
    suspend fun clearSearch(query: String)


    //каталог
    @Query("SELECT * FROM courses WHERE id IN (:ids)")
    suspend fun getCoursesByIds(ids: List<Int>): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)
}
