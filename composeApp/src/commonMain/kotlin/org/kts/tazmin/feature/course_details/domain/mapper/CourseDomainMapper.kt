package org.kts.tazmin.feature.course_details.domain.mapper

import org.kts.tazmin.feature.course_details.data.local.fullEntity.SectionFull
import org.kts.tazmin.feature.course_details.data.local.relation.UnitWithLesson
import org.kts.tazmin.feature.course_details.domain.entity.CourseLesson
import org.kts.tazmin.feature.course_details.domain.entity.CourseModule

interface CourseDomainMapper {
    fun mapSectionFull(entity: SectionFull): CourseModule
    fun mapUnitWithLesson(entity: UnitWithLesson): CourseLesson?
}
