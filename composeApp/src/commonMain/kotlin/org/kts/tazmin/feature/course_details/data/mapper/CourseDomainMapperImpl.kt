package org.kts.tazmin.feature.course_details.data.mapper

import org.kts.tazmin.feature.course_details.data.local.fullEntity.SectionFull
import org.kts.tazmin.feature.course_details.data.local.relation.UnitWithLesson
import org.kts.tazmin.feature.course_details.domain.entity.CourseLesson
import org.kts.tazmin.feature.course_details.domain.entity.CourseModule
import org.kts.tazmin.feature.course_details.domain.mapper.CourseDomainMapper

class CourseDomainMapperImpl : CourseDomainMapper {

    override fun mapSectionFull(entity: SectionFull): CourseModule {
        val lessons = entity.units
            .sortedBy { it.unit.position ?: Int.MAX_VALUE }
            .mapNotNull { mapUnitWithLesson(it) }

        return CourseModule(
            id = entity.section.id,
            title = entity.section.title,
            lessons = lessons,
            progress = calculateProgress(lessons)
        )
    }

    override fun mapUnitWithLesson(entity: UnitWithLesson): CourseLesson? {
        val lesson = entity.lesson ?: return null

        return CourseLesson(
            id = lesson.id,
            title = lesson.title
        )
    }

    private fun calculateProgress(lessons: List<CourseLesson>): Float {
        if (lessons.isEmpty()) return 0f
        return 0f // позже реальный прогресс TODO
    }
}
