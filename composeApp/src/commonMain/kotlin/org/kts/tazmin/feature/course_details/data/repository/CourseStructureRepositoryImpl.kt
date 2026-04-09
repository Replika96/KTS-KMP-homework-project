package org.kts.tazmin.feature.course_details.data.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.course_details.data.local.dao.CourseDetailsDao
import org.kts.tazmin.feature.course_details.data.local.dao.CourseStructureDao
import org.kts.tazmin.feature.course_details.data.network.CourseModulesApi
import org.kts.tazmin.feature.course_details.domain.entity.CourseModule
import org.kts.tazmin.feature.course_details.domain.mapper.CourseDomainMapper
import org.kts.tazmin.feature.course_details.domain.mapper.CourseStructureMapper
import org.kts.tazmin.feature.course_details.domain.repository.CourseStructureRepository

class CourseStructureRepositoryImpl(
    private val api: CourseModulesApi,
    private val structureDao: CourseStructureDao,
    private val courseDetailsDao: CourseDetailsDao,
    private val mapper: CourseStructureMapper,
    private val domainMapper: CourseDomainMapper,
) : CourseStructureRepository {

    override fun observeCourseStructure(courseId: Int): Flow<List<CourseModule>> =
        structureDao.observeCourseStructure(courseId)
            .map { sections ->
                Napier.d("Mapping ${sections.size} sections to domain")
                sections.map(domainMapper::mapSectionFull)
            }

    override suspend fun refreshCourseStructure(courseId: Int): Result<Unit> {
        Napier.i("Refreshing course structure for courseId=$courseId")

        return runCatchingCancellable {

            val sectionIds = courseDetailsDao.getCourse(courseId)
                ?.sectionIds
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?: emptyList()

            // загружаем секции
            val sectionDtos = if (sectionIds.isNotEmpty()) {
                api.getSections(sectionIds)
            } else emptyList()
            Napier.i("Loaded ${sectionDtos.size} sections")

            // загружаем units
            val unitIds = sectionDtos.flatMap { it.unitIds }.distinct()

            val unitDtos = if (unitIds.isNotEmpty()) {
                api.getUnits(unitIds)
            } else {
                emptyList()
            }
            Napier.i("Loaded ${unitDtos.size} units")

            // загружаем lessons
            val lessonIds = unitDtos.mapNotNull { it.lessonId }.distinct()
            Napier.d("Lesson IDs extracted: ${lessonIds.size}")

            val lessonDtos = if (lessonIds.isNotEmpty()) {
                api.getLessons(lessonIds)
            } else {
                emptyList()
            }
            Napier.i("Loaded ${lessonDtos.size} lessons")

            val sectionEntities = mapper.toSectionEntities(sectionDtos, courseId)

            val unitEntities = mapper.toUnitEntities(unitDtos, sectionDtos)

            val lessonEntities = mapper.toLessonEntities(lessonDtos)

            // транзакция
            Napier.d("Replacing course structure in DB…")
            structureDao.replaceCourseData(
                courseId = courseId,
                sections = sectionEntities,
                units = unitEntities,
                lessons = lessonEntities
            )
            Napier.i("Course structure replaced successfully for courseId=$courseId")
        }
    }
}
