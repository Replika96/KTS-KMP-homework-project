package org.kts.tazmin.feature.courses.data.repository

import coil3.network.HttpException
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.data.local.CourseDao
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.model.CourseDto
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.data.model.CoursesResponse
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.domain.entity.Course
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import kotlin.coroutines.cancellation.CancellationException

class CoursesRepositoryImpl(
    private val coursesApi: CoursesApi,
    private val courseDao: CourseDao,
    private val courseMapper: CourseMapper
) : CoursesRepository {

    override fun getCourses(page: Int, pageSize: Int): Flow<Resource<CoursesPage>> = flow {
        emit(Resource.Loading)

        // cначала грузим кэш
        val cached = loadCachedPage(page)
        if (cached == null) {
            emit(Resource.Loading)
        } else {
            emit(Resource.Success(cached, Source.CACHE))
        }

        // потом пробуем онлайн
        try {
            val remote = loadPageOnline(page, pageSize)
            emit(Resource.Success(remote, Source.REMOTE))
        } catch (e: Throwable) {

            if (e is CancellationException) throw e

            if (cached == null) {
                emit(Resource.Error(e.message ?: "Ошибка загрузки"))
            }
        }
    }

    // загружает страницу с сервера, перескакивая пустые страницы
    private suspend fun loadPageOnline(page: Int, pageSize: Int): CoursesPage {
        val response = findFirstNonEmptyPage(page, pageSize)

        val reviewMap = loadReviewMap(response.courses)
        val courses = courseMapper.fromDtoList(response.courses, reviewMap)

        saveCoursesToCache(courses, page)

        return CoursesPage(
            courses = courses,
            page = page,
            hasNext = response.meta.hasNext
        )
    }

    override suspend fun fetchCourses(page: Int, pageSize: Int): Resource<CoursesPage> {
        return try {
            val response = findFirstNonEmptyPage(originalPage = page, pageSize = pageSize)
            val reviewMap = loadReviewMap(response.courses)
            val courses = courseMapper.fromDtoList(response.courses, reviewMap)
            saveCoursesToCache(courses, page, null)

            val coursesPage = CoursesPage(
                courses = courses,
                page = page,
                hasNext = response.meta.hasNext
            )

            Resource.Success(
                data = coursesPage,
                source = Source.REMOTE
            )
        } catch (e: Throwable) {
            Napier.e("fetchCourses error: ${e::class.simpleName}: ${e.message}", e)

            if (e is CancellationException && !isNetworkTimeoutOrIo(e)) {
                throw e
            }

            val cached = if (isNetworkTimeoutOrIo(e)) {
                loadCachedPage(page)
            } else {
                null
            }
            Resource.Error(
                message = when {
                    isNetworkTimeoutOrIo(e) && cached != null -> "Ошибка сети. Показаны сохраненные результаты"
                    else -> e.message ?: "Ошибка загрузки курсов"
                },
                data = cached
            )
        }
    }

    // ищем первую непустую страницу, начиная с currentPage
    private suspend fun findFirstNonEmptyPage(
        originalPage: Int,
        pageSize: Int,
        currentPage: Int = originalPage,
        attempt: Int = 1
    ): CoursesResponse {

        if (attempt > 5) {
            throw IllegalStateException("Слишком много пустых страниц подряд")
        }

        val response = coursesApi.getCourses(currentPage, pageSize)

        return if (response.courses.isEmpty() && response.meta.hasNext) {
            findFirstNonEmptyPage(
                originalPage,
                pageSize,
                currentPage + 1,
                attempt + 1
            )
        } else {
            response
        }
    }

    private suspend fun loadCachedPage(page: Int): CoursesPage? {
        val cached = courseDao.getCoursesByPage(page)
        if (cached.isEmpty()) return null

        val maxPage = courseDao.getMaxCachedPage() ?: page

        return CoursesPage(
            courses = cached.map { courseMapper.fromEntity(it) },
            page = page,
            hasNext = page < maxPage
        )
    }

    private suspend fun saveCoursesToCache(
        courses: List<Course>,
        page: Int,
        query: String? = null
    ) {
        if (query == null) {
            courseDao.clearPage(page)
        } else {
            courseDao.clearSearch(query)
        }
        val entities = courses.map { courseMapper.toEntity(it, page, query) }
        courseDao.insertCourses(entities)
    }

    private suspend fun loadReviewMap(
        courseDtos: List<CourseDto>
    ): Map<Int, ReviewSummaryDto> = coroutineScope {

        courseDtos.mapNotNull { dto ->
            dto.reviewSummary?.let { id ->
                async {
                    try {
                        val review = coursesApi.getReviewSummary(id)
                        dto.id to review
                    } catch (e: Exception) {
                        Napier.w("Отзывы не загружены для курса ${dto.id}: ${e.message}")
                        null
                    }
                }
            }
        }.awaitAll().filterNotNull().toMap()
    }

    override suspend fun searchCourses(query: String, page: Int): Resource<CoursesPage> {
        return try {
            val pageData = loadSearchOnline(query, page)
            Resource.Success(
                data = pageData,
                source = Source.REMOTE
            )
        } catch (e: Throwable) {
            Napier.e("searchCourses error: ${e::class.simpleName}: ${e.message}", e)

            if (e is CancellationException) {
                throw e
            }

            val cached = if (isNetworkTimeoutOrIo(e)) {
                loadCachedSearchPage(query, page)
            } else {
                null
            }

            // если это сетевой таймаут / IO / HTTP то возвращаем кэш (fallback)
            Resource.Error(
                message = when {
                    isNetworkTimeoutOrIo(e) && cached != null -> "Ошибка сети. Показаны сохранённые результаты"
                    else -> e.message ?: "Ошибка поиска"
                },
                data = cached
            )
        }
    }

    private suspend fun loadSearchOnline(query: String, page: Int): CoursesPage {
        val response = findFirstNonEmptySearchPage(query, page)

        val reviewMap = loadReviewMap(response.courses)
        val courses = courseMapper.fromDtoList(response.courses, reviewMap)

        // сохраняем результаты поиска в кэш под ключом query
        saveCoursesToCache(courses, page, query)

        return CoursesPage(
            courses = courses,
            page = response.meta.page,
            hasNext = response.meta.hasNext
        )
    }

    private suspend fun findFirstNonEmptySearchPage(
        query: String,
        currentPage: Int,
        attempt: Int = 1
    ): CoursesResponse {
        if (attempt > 5) throw IllegalStateException("Слишком много пустых страниц подряд при поиске")

        val response = coursesApi.searchCourses(query, currentPage)

        return if (response.courses.isEmpty() && response.meta.hasNext) {
            findFirstNonEmptySearchPage(query, currentPage + 1, attempt + 1)
        } else {
            response
        }
    }

    private suspend fun loadCachedSearchPage(query: String, page: Int): CoursesPage? {
        val cachedEntities = courseDao.getSearchResults(query, page)
        if (cachedEntities.isEmpty()) return null

        val nextPageEntities = courseDao.getSearchResults(query, page + 1)
        val hasNext = nextPageEntities.isNotEmpty()

        return CoursesPage(
            courses = cachedEntities.map { courseMapper.fromEntity(it) },
            page = page,
            hasNext = hasNext
        )
    }

    private fun isNetworkTimeoutOrIo(e: Throwable): Boolean {
        if (e is HttpRequestTimeoutException) return true
        if (e is IOException) return true
        if (e is HttpException) return true

        if (e is CancellationException) {
            val msg = e.message ?: ""
            if (msg.contains("timeout", true) ||
                msg.contains("cancelled", true) ||
                msg.contains("connection", true)
            ) {
                return true
            }
        }

        return false
    }
}
