package org.kts.tazmin.feature.course_details.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.domain.entity.CourseHeader
import org.kts.tazmin.feature.course_details.domain.repository.CourseDetailsRepository

class GetCourseHeaderUseCase(
    private val repository: CourseDetailsRepository
) {
     operator fun invoke(courseId: Int): Flow<CourseHeader?> {
        return repository.observeHeader(courseId)
    }
}

