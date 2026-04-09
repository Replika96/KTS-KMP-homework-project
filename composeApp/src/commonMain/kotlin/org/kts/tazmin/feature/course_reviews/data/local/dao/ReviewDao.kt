package org.kts.tazmin.feature.course_reviews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_reviews.data.local.ReviewEntity

@Dao
interface ReviewDao {
    @Query(
        """
    SELECT * FROM reviews
    WHERE courseId = :courseId
    AND (:score IS NULL OR score = :score)
    ORDER BY 
        isPending DESC,
        localOrder DESC,
        createDate DESC
    """
    )
    fun observeReviews(
        courseId: Long,
        score: Int?
    ): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun getById(id: Long): ReviewEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity)

    @Query("""
    SELECT COUNT(*) FROM reviews
    WHERE courseId = :courseId
      AND (:score = -1 OR score = :score)
""")
    suspend fun getCount(courseId: Long, score: Int): Int


    @Update
    suspend fun update(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM reviews WHERE courseId = :courseId")
    suspend fun clearByCourse(courseId: Long)
}
