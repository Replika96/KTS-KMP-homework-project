package org.kts.tazmin.feature.course_details.domain.mapper

import org.kts.tazmin.feature.course_details.data.local.LessonEntity
import org.kts.tazmin.feature.course_details.data.local.SectionEntity
import org.kts.tazmin.feature.course_details.data.local.StepEntity
import org.kts.tazmin.feature.course_details.data.local.UnitEntity
import org.kts.tazmin.feature.course_details.data.response.LessonDto
import org.kts.tazmin.feature.course_details.data.response.SectionDto
import org.kts.tazmin.feature.course_details.data.response.StepDto
import org.kts.tazmin.feature.course_details.data.response.UnitDto

interface CourseStructureMapper {

    fun mapSectionDto(
        dto: SectionDto,
        courseId: Int,
        position: Int
    ): SectionEntity

    fun mapUnitDto(
        dto: UnitDto,
        sectionId: Int
    ): UnitEntity

    fun mapLessonDto(
        dto: LessonDto
    ): LessonEntity

    fun mapStepDto(
        dto: StepDto,
        lessonId: Int
    ): StepEntity

    fun toSectionEntities(
        dtos: List<SectionDto>,
        courseId: Int
    ): List<SectionEntity>

    fun toUnitEntities(
        dtos: List<UnitDto>,
        sections: List<SectionDto>
    ): List<UnitEntity>

    fun toLessonEntities(
        dtos: List<LessonDto>
    ): List<LessonEntity>

    fun toStepEntities(
        dtos: List<StepDto>,
        lessonId: Int
    ): List<StepEntity>
}

