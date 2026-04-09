package org.kts.tazmin.feature.course_details.data.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.course_details.data.local.dao.CourseDetailsDao
import org.kts.tazmin.feature.course_details.data.mapper.CourseCTAMapper
import org.kts.tazmin.feature.course_details.data.mapper.CourseHeaderMapper
import org.kts.tazmin.feature.course_details.data.mapper.CourseInfoMapper
import org.kts.tazmin.feature.course_details.data.mapper.toAuthorRefs
import org.kts.tazmin.feature.course_details.data.mapper.toEntity
import org.kts.tazmin.feature.course_details.data.mapper.toInstructorRefs
import org.kts.tazmin.feature.course_details.data.network.CourseInfoApi
import org.kts.tazmin.feature.course_details.domain.entity.CourseCTA
import org.kts.tazmin.feature.course_details.domain.entity.CourseHeader
import org.kts.tazmin.feature.course_details.domain.entity.CourseInfo
import org.kts.tazmin.feature.course_details.domain.repository.CourseDetailsRepository

class CourseDetailsRepositoryImpl(
    private val dao: CourseDetailsDao,
    private val api: CourseInfoApi,
    private val headerMapper: CourseHeaderMapper,
    private val infoMapper: CourseInfoMapper,
    private val ctaMapper: CourseCTAMapper,
) : CourseDetailsRepository {

    // единый источник данных из БД
    private fun observeFull(courseId: Int) =
        dao.observeCourse(courseId)

    override fun observeHeader(courseId: Int): Flow<CourseHeader?> =
        observeFull(courseId)
            .map { full ->
                full?.let { headerMapper.mapToDomain(it.course) }
            }

    override fun observeInfo(courseId: Int): Flow<CourseInfo?> =
        observeFull(courseId)
            .map { full ->
                full?.let {
                    infoMapper.mapToDomain(
                        it.course,
                        it.authors,
                        it.instructors
                    )
                }
            }

    override fun observeCTA(courseId: Int): Flow<CourseCTA?> =
        observeFull(courseId)
            .map { full ->
                full?.let {
                    ctaMapper.mapToDomain(
                        it.course,
                        it.enrollment,
                        it.progress,
                        it.review
                    )
                }
            }

    override suspend fun refresh(courseId: Int): Result<Unit> =
        coroutineScope {
            runCatchingCancellable {
                Napier.d("Refreshing course $courseId", tag = TAG)

                // загружаем сам курс (база для остальных вызовов)
                val course = api.getCourse(courseId)

                // параллельно грузим всё независимое
                val authorsDeferred = async {
                    api.getUsers(course.authors ?: emptyList())
                }

                val instructorsDeferred = async {
                    api.getUsers(course.instructorIds ?: emptyList())
                }

                val reviewDeferred = async {
                    course.reviewSummaryId?.let {
                        api.getReviewSummary(courseId)
                    }
                }
                Napier.i(" Progress: ${course.progressId}", tag = TAG)
                val progressDeferred = async {
                    course.progressId?.let {
                        api.getProgress(it)
                    }
                }
                val enrollmentDeferred = async {
                    api.getEnrollment(courseId)
                }

                val authors = authorsDeferred.await()
                val instructors = instructorsDeferred.await()
                val review = reviewDeferred.await()
                val progress = progressDeferred.await()
                val enrollment = enrollmentDeferred.await()
                Napier.i(" Progress: $progress", tag = TAG)
                // атомарная запись
                dao.insertCourseBundle(
                    course = course.toEntity(),
                    users = (authors + instructors).map { it.toEntity() },
                    authors = course.toAuthorRefs(),
                    instructors = course.toInstructorRefs(),
                    review = review?.toEntity(courseId),
                    progress = progress?.toEntity(),
                    enrollment = enrollment?.toEntity()
                )
                Napier.i("Refresh success", tag = TAG)
            }
        }.onFailure {
            Napier.e("Refresh failed", it, tag = TAG)
        }

    private companion object {
        const val TAG = "CourseDetailsRepository"
    }
}
