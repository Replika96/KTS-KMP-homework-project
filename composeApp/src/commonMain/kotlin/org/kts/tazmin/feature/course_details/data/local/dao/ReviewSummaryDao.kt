package org.kts.tazmin.feature.course_details.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.data.local.ReviewSummaryEntity

@Dao
interface ReviewSummaryDao {

    @Query("SELECT * FROM review_summary WHERE courseId = :courseId")
    fun observe(courseId: Int): Flow<ReviewSummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entity: ReviewSummaryEntity)
}
