package org.kts.tazmin.feature.courses.domain.usacase

import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository

class GetCoursesUseCase(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(
        page: Int,
        pageSize: Int = 20
    ): Result<CoursesPage> {
        return if (page < 1) {
            Result.failure(IllegalArgumentException("Page must be >= 1"))
        } else {
            repository.getCourses(page, pageSize)
        }
    }
}
