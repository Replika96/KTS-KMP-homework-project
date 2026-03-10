package org.kts.tazmin.feature.courses.domain.usacase

import org.kts.tazmin.feature.courses.domain.entity.Course
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository

class SearchCoursesUseCase(
    private val repository: CoursesRepository
) {

    suspend operator fun invoke(
        query: String,
        page: Int
    ): Result<List<Course>> {

        return repository.searchCourses(query, page)
            .map { it.courses }

    }
}