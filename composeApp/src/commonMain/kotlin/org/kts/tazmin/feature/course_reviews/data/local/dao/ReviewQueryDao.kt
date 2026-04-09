package org.kts.tazmin.feature.course_reviews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.kts.tazmin.feature.course_reviews.data.local.ReviewQueryCacheEntity

@Dao
interface ReviewQueryDao {
    @Query(
        """
    SELECT next FROM review_query_cache
    WHERE courseId = :courseId AND score = :score
    """
    )
    suspend fun getNext(
        courseId: Long,
        score: Int?
    ): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(query: ReviewQueryCacheEntity)

    @Query("DELETE FROM review_query_cache WHERE courseId = :courseId")
    suspend fun clearByCourse(courseId: Long)
}
