package org.kts.tazmin.feature.course_details.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.domain.entity.CourseModule

interface CourseStructureRepository {

    fun observeCourseStructure(courseId: Int): Flow<List<CourseModule>>

    suspend fun refreshCourseStructure(courseId: Int): Result<Unit>
}
