package org.kts.tazmin.feature.courses.domain.usecase

import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.presentation.state.CoursesResult

class SearchCoursesUseCase(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(query: String, page: Int): Resource<CoursesPage> {
        return repository.searchCourses(query, page)
    }
}
