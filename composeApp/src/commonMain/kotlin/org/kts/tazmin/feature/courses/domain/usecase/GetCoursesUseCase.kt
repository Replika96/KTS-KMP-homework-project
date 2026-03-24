package org.kts.tazmin.feature.courses.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.courses.data.model.CoursesPage
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository

class GetCoursesUseCase(
    private val repository: CoursesRepository
) {
    operator fun invoke(
        page: Int,
        pageSize: Int = 20
    ): Flow<Resource<CoursesPage>> {
        return repository.getCourses(page, pageSize)
    }
}

