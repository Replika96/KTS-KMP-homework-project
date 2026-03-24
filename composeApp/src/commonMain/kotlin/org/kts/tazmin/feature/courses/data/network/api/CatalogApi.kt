package org.kts.tazmin.feature.courses.data.network.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kts.tazmin.core.common.Config.baseUrl
import org.kts.tazmin.feature.courses.data.model.CatalogBlocksResponse
import org.kts.tazmin.feature.courses.data.model.CoursesResponse

class CatalogApi(
    private val client: HttpClient
) {

    suspend fun getCatalogBlocks(): CatalogBlocksResponse {
        return client.get("$baseUrl/api/catalog-blocks")
            .body()
    }

//    suspend fun getCourseLists(ids: List<Int>): CourseListsResponse {
//        return client.get("$baseUrl/api/course-lists") {
//            ids.forEach { id ->
//                parameter("ids[]", id)
//            }
//        }.body()
//    }

    suspend fun getCourses(ids: List<Int>): CoursesResponse {
        return client.get("$baseUrl/api/courses") {
            ids.forEach { id ->
                parameter("ids[]", id)
            }
        }.body()
    }
}
