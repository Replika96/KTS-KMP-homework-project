package org.kts.tazmin.feature.course_reviews.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_pending_actions")
data class PendingActionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val type: String, // "create", "vote"

    val reviewId: Long?,

    val payload: String, // json

    val createdAt: Long
)
