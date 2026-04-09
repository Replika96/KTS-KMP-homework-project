package org.kts.tazmin.feature.course_details.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.data.local.ProgressEntity

@Dao
interface ProgressDao {

    @Query("SELECT * FROM progress WHERE id = :id")
    fun observe(id: String): Flow<ProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ProgressEntity)
}
