package org.kts.tazmin.feature.courses.data.repository

import coil3.network.HttpException
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.data.local.CourseDao
import org.kts.tazmin.feature.courses.data.mapper.CourseDbMapper.toDomain
import org.kts.tazmin.feature.courses.data.mapper.CourseDbMapper.toEntity
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.model.CourseDto
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.data.model.CoursesResponse
import org.kts.tazmin.feature.courses.data.model.ReviewSummaryDto
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.domain.entity.Course
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.presentation.state.CoursesResult
import kotlin.coroutines.cancellation.CancellationException

class CoursesRepositoryImpl(
    private val api: CoursesApi,
    private val courseDao: CourseDao,
    private val courseMapper: CourseMapper
) : CoursesRepository {

    override fun getCourses(page: Int, pageSize: Int): Flow<Resource<CoursesPage>> = flow {
        emit(Resource.Loading)

        // cначала грузим кэш
        val cached = loadCachedPage(page)
        if (cached != null) {
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
        val courses = courseMapper.mapToDomainList(response.courses, reviewMap)

        saveCoursesToCache(courses, page)

        return CoursesPage(
            courses = courses,
            page = page,
            hasNext = response.meta.hasNext
        )
    }

    override suspend fun fetchCourses(page: Int, pageSize: Int): CoursesResult {
        return try {
            val response = findFirstNonEmptyPage(originalPage = page, pageSize = pageSize)
            val reviewMap = loadReviewMap(response.courses)
            val courses = courseMapper.mapToDomainList(response.courses, reviewMap)
            saveCoursesToCache(courses, page, null)

            CoursesResult.Success(
                data = CoursesPage(courses = courses, page = page, hasNext = response.meta.hasNext),
                source = Source.REMOTE
            )
        } catch (e: Throwable) {
            Napier.e("fetchCourses error: ${e::class.simpleName}: ${e.message}", e)

            if (e is CancellationException && !isNetworkTimeoutOrIo(e)) {
                throw e
            }

            if (isNetworkTimeoutOrIo(e)) {
                val cached = loadCachedPage(page)
                return if (cached != null) {
                    CoursesResult.Error(
                        cachedData = cached,
                        message = "Ошибка сети. Показаны сохраненные результаты"
                    )
                } else {
                    CoursesResult.Error(message = e.message ?: "Ошибка загрузки курсов")
                }
            }

            return CoursesResult.Error(message = e.message ?: "Ошибка загрузки курсов")
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

        val response = api.getCourses(currentPage, pageSize)

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
            courses = cached.map { it.toDomain() },
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
        val entities = courses.map { it.toEntity(page, query) }
        courseDao.insertCourses(entities)
    }


    private suspend fun loadReviewMap(courseDtos: List<CourseDto>): Map<Int, ReviewSummaryDto> {
        return courseDtos.mapNotNull { dto ->
            dto.reviewSummary?.let { id ->
                runCatching { api.getReviewSummary(id) }.getOrNull()?.let { review ->
                    dto.id to review
                }
            }
        }.toMap()
    }

    override suspend fun searchCourses(query: String, page: Int): CoursesResult {
        return try {
            val pageData = loadSearchOnline(query, page)
            CoursesResult.Success(
                data = pageData,
                source = Source.REMOTE
            )
        } catch (e: Throwable) {
            Napier.e("searchCourses error: ${e::class.simpleName}: ${e.message}", e)

            if (e is CancellationException && !isNetworkTimeoutOrIo(e)) {
                throw e
            }

            // если это сетевой таймаут / IO / HTTP то возвращаем кэш (fallback)
            if (isNetworkTimeoutOrIo(e)) {
                val cached = loadCachedSearchPage(query, page)
                return if (cached != null) {
                    CoursesResult.Error(
                        cachedData = cached,
                        message = "Ошибка сети. Показаны сохранённые результаты"
                    )
                } else {
                    CoursesResult.Error(
                        message = e.message ?: "Ошибка поиска",
                        cachedData = null
                    )
                }
            }

            // прочие ошибки — общая обработка
            return CoursesResult.Error(
                message = e.message ?: "Ошибка поиска",
                cachedData = null
            )
        }
    }

    private suspend fun loadSearchOnline(query: String, page: Int): CoursesPage {
        val response = findFirstNonEmptySearchPage(query, page)

        val reviewMap = loadReviewMap(response.courses)
        val courses = courseMapper.mapToDomainList(response.courses, reviewMap)

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

        val response = api.searchCourses(query, currentPage)

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
            courses = cachedEntities.map { it.toDomain() },
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

