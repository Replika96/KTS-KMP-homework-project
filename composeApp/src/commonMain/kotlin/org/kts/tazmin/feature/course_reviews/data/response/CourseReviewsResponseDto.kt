package org.kts.tazmin.feature.course_reviews.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kts.tazmin.feature.catalog.data.model.Meta

@Serializable
data class CourseReviewsResponseDto(
    val meta: Meta,

    @SerialName("course-reviews")
    val courseReviews: List<CourseReviewDto>
)

@Serializable
data class CourseReviewDto(
    val id: Long,
    val course: String,
    val user: String,
    val score: Int,
    val text: String,

    @SerialName("reply_text")
    val replyText: String?,

    @SerialName("create_date")
    val createDate: String,

    @SerialName("update_date")
    val updateDate: String,

    @SerialName("epic_count")
    val epicCount: Int,

    @SerialName("abuse_count")
    val abuseCount: Int,

    @SerialName("vote_delta")
    val voteDelta: Int,

    val vote: String?
)
@Serializable
data class ReviewVoteRequest(
    val vote: String // "up", "down", null
)
