package org.kts.tazmin.feature.courses.domain.repository

import org.kts.tazmin.feature.courses.data.model.CoursesPage

interface CoursesRepository {
    suspend fun getCourses(
        page: Int,
        pageSize: Int
    ): Result<CoursesPage>

    suspend fun searchCourses(
        query: String,
        page: Int
    ): Result<CoursesPage>
}