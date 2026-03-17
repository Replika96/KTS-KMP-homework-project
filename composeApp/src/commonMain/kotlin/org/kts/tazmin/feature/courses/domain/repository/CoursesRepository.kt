package org.kts.tazmin.feature.courses.domain.repository

import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.presentation.state.CoursesResult

interface CoursesRepository {
    suspend fun getCourses(
        page: Int,
        pageSize: Int
    ): CoursesResult

    suspend fun searchCourses(
        query: String,
        page: Int
    ): CoursesResult
}
