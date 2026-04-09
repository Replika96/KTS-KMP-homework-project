package org.kts.tazmin.feature.catalog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.catalog.data.local.CourseEntity
import org.kts.tazmin.feature.catalog.data.local.CourseListPageEntity
import org.kts.tazmin.feature.catalog.data.local.ref.CourseListCourseRef

@Dao
interface CourseListDao {

    // наблюдаем за курсами конкретного списка
    @Query(
        """
        SELECT c.* FROM courses c
        INNER JOIN course_list_courses clc ON c.id = clc.courseId
        WHERE clc.courseListId = :courseListId
        ORDER BY clc.position ASC
    """
    )
    fun observeCourses(courseListId: Int): Flow<List<CourseEntity>>

    // кэш страниц
    @Query("SELECT * FROM course_list_page_cache WHERE courseListId = :courseListId")
    suspend fun getPage(courseListId: Int): CourseListPageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: CourseListPageEntity)

    // связь курс ↔ список
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseRefs(refs: List<CourseListCourseRef>)

    @Query("DELETE FROM course_list_courses WHERE courseListId = :courseListId")
    suspend fun clearCourses(courseListId: Int)

    @Query("DELETE FROM course_list_page_cache WHERE courseListId = :courseListId")
    suspend fun clearPage(courseListId: Int)

    @Transaction
    suspend fun replaceCourses(
        courseListId: Int,
        refs: List<CourseListCourseRef>
    ) {
        clearCourses(courseListId)
        clearPage(courseListId)
        insertCourseRefs(refs)
    }
}
