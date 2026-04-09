package org.kts.tazmin.feature.catalog.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.catalog.data.local.dao.MyCoursesDao
import org.kts.tazmin.feature.catalog.data.model.CoursesPage
import org.kts.tazmin.feature.catalog.domain.mapper.MyCourseMapper
import org.kts.tazmin.feature.catalog.domain.network.CoursesApi
import org.kts.tazmin.feature.catalog.domain.repository.MyCoursesRepository

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
                    data = CoursesPage(
                        courses = cached.map(mapper::toDomain),
                        1,
                        false),
                    source = Source.CACHE
                )
            )
        }

        val result = runCatchingCancellable {
            val pairs = coursesApi.getUserCourses()

            val domain = pairs.map { (dto, progress) ->
                mapper.toDomain(dto, progress)
            }

            myCoursesDao.clear()
            myCoursesDao.insertMyCourses(domain.map(mapper::toEntity))

            CoursesPage(domain, 1, false)
        }

        result.fold(
            onSuccess = { page ->
                emit(Resource.Success(data = page, source = Source.REMOTE))
            },
            onFailure = { e ->
                emit(
                    Resource.Error(
                        message = e as AppError,
                        data = cached?.let {
                            CoursesPage(
                                courses = it.map(mapper::toDomain),
                                1,
                                false)
                        }
                    )
                )
            }
        )
    }
}
