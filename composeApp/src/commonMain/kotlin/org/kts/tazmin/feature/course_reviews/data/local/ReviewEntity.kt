package org.kts.tazmin.feature.course_reviews.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    indices = [
        Index("courseId"),
        Index("score"),
        Index("createDate")
    ]
)
data class ReviewEntity(

    @PrimaryKey
    val id: Long,

    val courseId: Long,
    val userId: Long,

    val score: Int,
    val text: String,

    val replyText: String?,

    val createDate: String,
    val updateDate: String,

    val epicCount: Int,
    val abuseCount: Int,
    val voteDelta: Int,

    val vote: String?, // "up", "down", null

    // offline-first
    val isPending: Boolean = false, // локально созданный отзыв

    val localOrder: Long = 0L // для стабильного порядка
)
