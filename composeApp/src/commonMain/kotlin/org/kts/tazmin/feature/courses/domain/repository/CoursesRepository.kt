package org.kts.tazmin.feature.courses.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.presentation.state.CoursesResult

interface CoursesRepository {
    fun getCourses(
        page: Int,
        pageSize: Int
    ): Flow<Resource<CoursesPage>>

    suspend fun searchCourses(
        query: String,
        page: Int
    ): CoursesResult

    suspend fun fetchCourses(
        page: Int,
        pageSize: Int
    ): CoursesResult
}
