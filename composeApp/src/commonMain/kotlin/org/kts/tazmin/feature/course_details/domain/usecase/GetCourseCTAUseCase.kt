package org.kts.tazmin.feature.course_details.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.domain.entity.CourseCTA
import org.kts.tazmin.feature.course_details.domain.repository.CourseDetailsRepository

class GetCourseCTAUseCase(
    private val repository: CourseDetailsRepository
) {
    operator fun invoke(courseId: Int): Flow<CourseCTA?> {
        return repository.observeCTA(courseId)
    }
}

