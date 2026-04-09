package org.kts.tazmin.feature.catalog.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.catalog.data.model.CoursesPage

interface MyCoursesRepository {

    fun getMyCourses(): Flow<Resource<CoursesPage>>

}
