package org.kts.tazmin.feature.catalog.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.catalog.data.model.CoursesPage
import org.kts.tazmin.feature.catalog.domain.repository.MyCoursesRepository

class GetMyCoursesUseCase(
    private val myCoursesRepository: MyCoursesRepository
) {
    operator fun invoke(): Flow<Resource<CoursesPage>> {
        return myCoursesRepository.getMyCourses()
    }
}
