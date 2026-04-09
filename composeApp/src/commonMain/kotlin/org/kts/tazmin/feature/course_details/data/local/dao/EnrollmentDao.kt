package org.kts.tazmin.feature.course_details.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.data.local.EnrollmentEntity

@Dao
interface EnrollmentDao {

    @Query("SELECT * FROM enrollment WHERE courseId = :courseId")
    fun observe(courseId: Int): Flow<EnrollmentEntity?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entity: EnrollmentEntity)
}
