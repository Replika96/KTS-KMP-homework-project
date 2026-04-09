package org.kts.tazmin.feature.course_reviews.domain.model

data class Review(
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
    val vote: String?,
    val isPending: Boolean
)
