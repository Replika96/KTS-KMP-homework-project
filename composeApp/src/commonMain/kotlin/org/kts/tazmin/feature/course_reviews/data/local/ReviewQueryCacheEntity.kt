package org.kts.tazmin.feature.course_reviews.data.local

import androidx.room.Entity

@Entity(
    tableName = "review_query_cache",
    primaryKeys = ["courseId", "score"]
)
data class ReviewQueryCacheEntity(
    val courseId: Long,
    val score: Int,
    val next: String?
)
