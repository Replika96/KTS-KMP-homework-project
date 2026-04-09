package org.kts.tazmin.feature.catalog.domain.network

import org.kts.tazmin.feature.catalog.data.model.CourseDto
import org.kts.tazmin.feature.catalog.data.model.CoursesResponse
import org.kts.tazmin.feature.catalog.data.model.ProgressDto
import org.kts.tazmin.feature.catalog.data.model.ReviewSummaryDto

interface CoursesApi {

    suspend fun getCourses(
        page: Int = 2,
        pageSize: Int = 20
    ): CoursesResponse

    suspend fun searchCourses(
        query: String,
        page: Int = 2,
        pageSize: Int = 20
    ): CoursesResponse

    suspend fun getReviewSummary(id: Int): ReviewSummaryDto

    suspend fun getUserCourses(): List<Pair<CourseDto, ProgressDto?>>
}
