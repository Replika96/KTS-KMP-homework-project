package org.kts.tazmin.feature.course_details.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.kts.tazmin.core.common.Config
import org.kts.tazmin.feature.course_details.data.response.LessonDto
import org.kts.tazmin.feature.course_details.data.response.LessonsResponse
import org.kts.tazmin.feature.course_details.data.response.SectionDto
import org.kts.tazmin.feature.course_details.data.response.SectionsResponse
import org.kts.tazmin.feature.course_details.data.response.StepDto
import org.kts.tazmin.feature.course_details.data.response.StepsResponse
import org.kts.tazmin.feature.course_details.data.response.UnitDto
import org.kts.tazmin.feature.course_details.data.response.UnitsResponse

class CourseModulesApi(
    private val client: HttpClient
) {
    suspend fun getSections(ids: List<Int>): List<SectionDto> =
        ids.chunked(30).flatMap { chunk ->
            client.get("${Config.baseUrl}/api/sections") {
                chunk.forEach { id ->
                    parameter("ids[]", id)
                }
            }.body<SectionsResponse>().sections
        }

    suspend fun getUnits(unitIds: List<Int>): List<UnitDto> = coroutineScope {
        unitIds.chunked(20).map { chunk ->
            async {
                client.get("${Config.baseUrl}/api/units") {
                    chunk.forEach { parameter("ids[]", it) }
                }.body<UnitsResponse>().units
            }
        }.awaitAll().flatten()
    }

    suspend fun getLessons(lessonIds: List<Int>): List<LessonDto> =
        lessonIds.chunked(20).flatMap { chunk ->
            client.get("${Config.baseUrl}/api/lessons") {
                chunk.forEach { parameter("ids[]", it) }
            }.body<LessonsResponse>().lessons
        }

    suspend fun getSteps(lessonIds: List<Int>): List<StepDto> = coroutineScope {
        lessonIds.map { lessonId ->
            async {
                client.get("${Config.baseUrl}/api/steps") {
                    parameter("lesson", lessonId)
                }.body<StepsResponse>().steps
            }
        }.awaitAll().flatten()
    }
}
