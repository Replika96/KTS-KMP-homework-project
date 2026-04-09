package org.kts.tazmin.feature.catalog.domain.usecase

import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.catalog.data.model.CoursesPage
import org.kts.tazmin.feature.catalog.domain.repository.CoursesRepository

class SearchCoursesUseCase(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(query: String, page: Int): Resource<CoursesPage> {
        return repository.searchCourses(query, page)
    }
}
