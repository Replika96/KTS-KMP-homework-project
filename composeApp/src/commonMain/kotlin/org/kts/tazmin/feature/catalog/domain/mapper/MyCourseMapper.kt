package org.kts.tazmin.feature.catalog.domain.mapper

import org.kts.tazmin.feature.catalog.data.local.MyCourseEntity
import org.kts.tazmin.feature.catalog.data.model.CourseDto
import org.kts.tazmin.feature.catalog.data.model.ProgressDto
import org.kts.tazmin.feature.catalog.domain.entity.Course

interface MyCourseMapper {

    fun toDomain(dto: CourseDto, progressDto: ProgressDto?): Course


    fun toEntity(course: Course): MyCourseEntity


    fun toDomain(entity: MyCourseEntity): Course
}
