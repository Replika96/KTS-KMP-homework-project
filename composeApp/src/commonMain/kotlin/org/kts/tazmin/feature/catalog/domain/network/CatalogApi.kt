package org.kts.tazmin.feature.catalog.domain.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kts.tazmin.core.common.Config.baseUrl
import org.kts.tazmin.feature.catalog.data.model.CatalogBlocksResponse
import org.kts.tazmin.feature.catalog.data.model.CoursesResponse

interface CatalogApi{

    suspend fun getCatalogBlocks(): CatalogBlocksResponse

    suspend fun getCourses(ids: List<Int>): CoursesResponse
    suspend fun getCoursesByList(
        courseListId: Int,
        page: Int = 1
    ): CoursesResponse
}
