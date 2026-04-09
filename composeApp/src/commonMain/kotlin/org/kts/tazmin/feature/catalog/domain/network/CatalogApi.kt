package org.kts.tazmin.feature.catalog.domain.network

import org.kts.tazmin.feature.catalog.data.model.CatalogBlocksResponse
import org.kts.tazmin.feature.catalog.data.model.CoursesResponse

interface CatalogApi {

    suspend fun getCatalogBlocks(): CatalogBlocksResponse

    suspend fun getCourses(ids: List<Int>): CoursesResponse
    suspend fun getCoursesByList(
        courseListId: Int,
        page: Int = 1
    ): CoursesResponse
}
