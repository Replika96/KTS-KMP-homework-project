package org.kts.tazmin.feature.courses.domain.usacase

import org.kts.tazmin.feature.courses.data.model.CoursesResponse
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository

class GetCoursesUseCase(private val coursesRepository: CoursesRepository) {
    // пока что простой usecase
    suspend operator fun invoke(
        page: Int,
        limit: Int = 20
    ): Result<CoursesResponse> {
        return coursesRepository.getCourses(
            page = page,
            limit = limit
        )
    }
}
