package org.kts.tazmin.feature.courses.domain.repository

import org.kts.tazmin.feature.courses.data.model.CoursesResponse

interface CoursesRepository {
    suspend fun getCourses(
        page: Int,
        limit: Int): Result<CoursesResponse>
}
