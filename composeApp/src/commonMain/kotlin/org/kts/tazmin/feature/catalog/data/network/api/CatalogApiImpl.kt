package org.kts.tazmin.feature.catalog.data.network.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kts.tazmin.core.common.Config.baseUrl
import org.kts.tazmin.feature.catalog.data.model.CatalogBlocksResponse
import org.kts.tazmin.feature.catalog.data.model.CoursesResponse
import org.kts.tazmin.feature.catalog.domain.network.CatalogApi

class CatalogApiImpl(
    private val client: HttpClient
) : CatalogApi {

    override suspend fun getCatalogBlocks(): CatalogBlocksResponse {
        return client.get("$baseUrl/api/catalog-blocks")
            .body()
    }

    override suspend fun getCourses(ids: List<Int>): CoursesResponse {
        return client.get("$baseUrl/api/courses") {
            ids.forEach { id ->
                parameter("ids[]", id)
            }
        }.body()
    }

    override suspend fun getCoursesByList(
        courseListId: Int,
        page: Int
    ): CoursesResponse = client.get("$baseUrl/api/courses") {
        parameter("course_list", courseListId)
        parameter("page", page)
    }.body()
}
