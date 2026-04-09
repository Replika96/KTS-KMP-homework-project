package org.kts.tazmin.feature.course_details.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.domain.entity.CourseInfo
import org.kts.tazmin.feature.course_details.domain.repository.CourseDetailsRepository

class GetCourseInfoUseCase(
    private val repository: CourseDetailsRepository
) {
    operator fun invoke(courseId: Int): Flow<CourseInfo?> {
        return repository.observeInfo(courseId)
    }
}
