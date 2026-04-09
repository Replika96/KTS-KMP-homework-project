package org.kts.tazmin.feature.course_details.data.mapper

import org.kts.tazmin.feature.course_details.data.local.LessonEntity
import org.kts.tazmin.feature.course_details.data.local.SectionEntity
import org.kts.tazmin.feature.course_details.data.local.StepEntity
import org.kts.tazmin.feature.course_details.data.local.UnitEntity
import org.kts.tazmin.feature.course_details.data.response.LessonDto
import org.kts.tazmin.feature.course_details.data.response.SectionDto
import org.kts.tazmin.feature.course_details.data.response.StepDto
import org.kts.tazmin.feature.course_details.data.response.UnitDto
import org.kts.tazmin.feature.course_details.domain.mapper.CourseStructureMapper

class CourseStructureMapperImpl : CourseStructureMapper {

    override fun mapSectionDto(
        dto: SectionDto,
        courseId: Int,
        position: Int
    ): SectionEntity {
        return SectionEntity(
            id = dto.id,
            courseId = courseId,
            title = dto.title ?: "",
            position = position
        )
    }

    override fun mapUnitDto(
        dto: UnitDto,
        sectionId: Int
    ): UnitEntity {
        return UnitEntity(
            id = dto.id,
            sectionId = sectionId,
            lessonId = dto.lessonId, // может быть null это нормально
            position = dto.position ?: 0
        )
    }

    override fun mapLessonDto(
        dto: LessonDto
    ): LessonEntity {
        return LessonEntity(
            id = dto.id,
            title = dto.title ?: "Урок ${dto.id}",
            slug = null
        )
    }
    override fun mapStepDto(dto: StepDto, lessonId: Int): StepEntity =
        StepEntity(
            id = dto.id,
            lessonId = lessonId,
            name = dto.block.name,
            text = dto.block.text
        )

    override fun toSectionEntities(
        dtos: List<SectionDto>,
        courseId: Int
    ): List<SectionEntity> =
        dtos.map { dto ->
            mapSectionDto(dto, courseId, dto.position)
        }

    override fun toUnitEntities(
        dtos: List<UnitDto>,
        sections: List<SectionDto>
    ): List<UnitEntity> =
        dtos
            .sortedBy { it.position ?: Int.MAX_VALUE }
            .map { dto ->
                val sectionId = findSectionId(dto, sections)
                mapUnitDto(dto, sectionId)
            }

    override fun toLessonEntities(
        dtos: List<LessonDto>
    ): List<LessonEntity> =
        dtos.map(::mapLessonDto)

    override fun toStepEntities(
        dtos: List<StepDto>,
        lessonId: Int
    ): List<StepEntity> =
        dtos.map { mapStepDto(it, lessonId) }

    private fun findSectionId(unit: UnitDto, sections: List<SectionDto>): Int =
        sections.firstOrNull { unit.id in it.unitIds }?.id
            ?: error("Unit ${unit.id} not found in any section")
}
