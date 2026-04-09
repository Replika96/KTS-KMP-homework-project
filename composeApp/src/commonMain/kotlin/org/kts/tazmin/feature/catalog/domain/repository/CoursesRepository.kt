package org.kts.tazmin.feature.catalog.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.catalog.data.model.CoursesPage

interface CoursesRepository {
    fun getCourses(
        page: Int,
        pageSize: Int
    ): Flow<Resource<CoursesPage>>

    suspend fun searchCourses(
        query: String,
        page: Int
    ): Resource<CoursesPage>

    suspend fun fetchCourses(
        page: Int,
        pageSize: Int
    ): Resource<CoursesPage>
}
