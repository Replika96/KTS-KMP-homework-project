package org.kts.tazmin.feature.course_reviews.data.repository

import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.course_reviews.data.local.ReviewEntity
import org.kts.tazmin.feature.course_reviews.data.local.ReviewQueryCacheEntity
import org.kts.tazmin.feature.course_reviews.data.local.dao.ReviewDao
import org.kts.tazmin.feature.course_reviews.data.local.dao.ReviewQueryDao
import org.kts.tazmin.feature.course_reviews.data.response.ReviewVoteRequest
import org.kts.tazmin.feature.course_reviews.domain.mapper.ReviewMapper
import org.kts.tazmin.feature.course_reviews.domain.model.Review
import org.kts.tazmin.feature.course_reviews.domain.network.ReviewApi
import org.kts.tazmin.feature.course_reviews.domain.repository.ReviewRepository
import kotlin.time.Clock

import io.github.aakira.napier.Napier

class ReviewRepositoryImpl(
    private val api: ReviewApi,
    private val reviewDao: ReviewDao,
    private val queryDao: ReviewQueryDao,
    private val mapper: ReviewMapper,
) : ReviewRepository {

    companion object {
        const val PAGE_SIZE = 20
    }

    override fun observeReviews(courseId: Long, score: Int?): Flow<List<Review>> =
        reviewDao.observeReviews(courseId, score)
            .map { entities -> entities.map(mapper::toDomain) }

    override suspend fun refreshReviews(courseId: Long, score: Int?): Result<Boolean> =
        runCatchingCancellable {
            Napier.d("Refreshing reviews: course=$courseId score=$score")

            val normalizedScore = score ?: -1

            val response = api.getCourseReviews(
                courseId = courseId,
                page = 1,
                pageSize = PAGE_SIZE,
                score = score
            )

            Napier.d("Loaded ${response.courseReviews.size} reviews, hasNext=${response.meta.hasNext}")

            val entities = response.courseReviews.mapIndexed { index, dto ->
                mapper.toEntity(dto, courseId, localOrder = index.toLong())
            }
            val nextPage = if (response.meta.hasNext) response.meta.page + 1 else null
            //todo
            reviewDao.clearByCourse(courseId)
            queryDao.clearByCourse(courseId)

            reviewDao.insertAll(entities)
            queryDao.insert(
                ReviewQueryCacheEntity(
                    courseId = courseId,
                    score = normalizedScore,
                    next = nextPage?.toString()
                )
            )

            Napier.d("Refresh complete. nextPage=$nextPage")
            nextPage != null
        }.onFailure {
            Napier.e("Refresh failed: course=$courseId score=$score", it)
        }

    override suspend fun loadNextPage(courseId: Long, score: Int?): Result<Boolean> =
        runCatchingCancellable {
            val normalizedScore = score ?: -1

            val nextUrl = queryDao.getNext(courseId, score)
            if (nextUrl == null) {
                Napier.d("No next page for course=$courseId score=$score")
                return@runCatchingCancellable false
            }

            val page = nextUrl.toPageNumber()
            if (page == null) {
                Napier.w("Invalid nextUrl=$nextUrl for course=$courseId")
                return@runCatchingCancellable false
            }

            Napier.d("Loading next page: course=$courseId score=$score page=$page")

            val response = api.getCourseReviews(
                courseId = courseId,
                page = page,
                pageSize = PAGE_SIZE,
                score = score
            )

            val currentCount = reviewDao.getCount(courseId, normalizedScore)

            Napier.d("Appending ${response.courseReviews.size} reviews (currentCount=$currentCount)")

            val entities = response.courseReviews.mapIndexed { index, dto ->
                mapper.toEntity(dto, courseId, localOrder = (currentCount + index).toLong())
            }
            val nextPage = if (response.meta.hasNext) response.meta.page + 1 else null

            reviewDao.insertAll(entities)
            queryDao.insert(
                ReviewQueryCacheEntity(
                    courseId = courseId,
                    score = normalizedScore,
                    next = nextPage?.toString()
                )
            )

            Napier.d("Next page loaded. nextPage=$nextPage")
            nextPage != null
        }.onFailure {
            Napier.e("loadNextPage failed: course=$courseId score=$score", it)
        }

    override suspend fun createReview(courseId: Long, score: Int, text: String): Result<Unit> =
        runCatchingCancellable {
            Napier.d("Creating review: course=$courseId score=$score text='${text.take(30)}...'")

            val tempId = -Clock.System.now().toEpochMilliseconds()
            val pending = ReviewEntity(
                id = tempId,
                courseId = courseId,
                userId = 0L,
                score = score,
                text = text,
                replyText = null,
                createDate = "",
                updateDate = "",
                epicCount = 0,
                abuseCount = 0,
                voteDelta = 0,
                vote = null,
                isPending = true,
                localOrder = Long.MAX_VALUE
            )
            reviewDao.insert(pending)

            val dto = api.createReview(courseId, score, text)

            reviewDao.deleteById(tempId)
            reviewDao.insert(mapper.toEntity(dto, courseId))

            Napier.d("Review created successfully: id=${dto.id}")
        }.onFailure {
            Napier.e("createReview failed: course=$courseId", it)
        }

    override suspend fun voteReview(reviewId: Long, vote: String): Result<Unit> =
        runCatchingCancellable {
            Napier.d("Voting review: id=$reviewId vote=$vote")

            val current = reviewDao.getById(reviewId)
            if (current == null) {
                Napier.w("voteReview: review not found id=$reviewId")
                return@runCatchingCancellable
            }

            val updated = when (vote) {
                "up" -> when (current.vote) {
                    "up" -> current
                    "down" -> current.copy(vote = "up", voteDelta = current.voteDelta + 2)
                    else -> current.copy(vote = "up", voteDelta = current.voteDelta + 1)
                }
                "down" -> when (current.vote) {
                    "down" -> current
                    "up" -> current.copy(vote = "down", voteDelta = current.voteDelta - 2)
                    else -> current.copy(vote = "down", voteDelta = current.voteDelta - 1)
                }
                else -> current
            }

            reviewDao.update(updated)

            runCatching {
                api.voteReview(reviewId, ReviewVoteRequest(vote))
            }.onFailure {
                Napier.e("voteReview failed, rolling back id=$reviewId", it)
                reviewDao.update(current)
                throw it
            }

            Napier.d("voteReview success id=$reviewId")
        }

    private fun String.toPageNumber(): Int? =
        runCatching {
            Url(this).parameters["page"]?.toInt()
        }.getOrNull()
}
