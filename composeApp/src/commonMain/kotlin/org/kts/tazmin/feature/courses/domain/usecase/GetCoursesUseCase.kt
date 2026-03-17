package org.kts.tazmin.feature.courses.domain.usecase

import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.presentation.state.CoursesResult

class GetCoursesUseCase(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(
        page: Int,
        pageSize: Int = 20
    ): CoursesResult {
        return repository.getCourses(page, pageSize)
    }
}
