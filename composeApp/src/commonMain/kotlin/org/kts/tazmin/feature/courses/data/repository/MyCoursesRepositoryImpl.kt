package org.kts.tazmin.feature.courses.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.data.local.MyCoursesDao
import org.kts.tazmin.feature.courses.data.mapper.MyCourseMapper
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.domain.repository.MyCoursesRepository
import kotlin.coroutines.cancellation.CancellationException

class MyCoursesRepositoryImpl(
    private val coursesApi: CoursesApi,
    private val myCoursesDao: MyCoursesDao,
    private val mapper: MyCourseMapper
) : MyCoursesRepository {

    override fun getMyCourses(): Flow<Resource<CoursesPage>> = flow {
        emit(Resource.Loading)

        val cached = myCoursesDao.observeMyCourses().firstOrNull()

        if (!cached.isNullOrEmpty()) {
            emit(
                Resource.Success(
                    data = CoursesPage(cached.map(mapper::toDomain), 1, false),
                    source = Source.CACHE
                )
            )
        }

        try {
            val pairs = coursesApi.getUserCourses()

            val domain = pairs.map { (dto, progress) ->
                mapper.toDomain(dto, progress)
            }

            myCoursesDao.clear()
            myCoursesDao.insertMyCourses(domain.map(mapper::toEntity))

            emit(
                Resource.Success(
                    data = CoursesPage(domain, 1, false),
                    source = Source.REMOTE
                )
            )

        } catch (e: Throwable) {
            if (e is CancellationException) throw e

            emit(
                Resource.Error(
                    message = e.message ?: "Ошибка загрузки",
                    data = cached?.let {
                        CoursesPage(it.map(mapper::toDomain), 1, false)
                    }
                )
            )
        }
    }
}
