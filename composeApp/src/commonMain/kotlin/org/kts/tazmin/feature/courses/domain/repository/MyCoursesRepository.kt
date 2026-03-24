package org.kts.tazmin.feature.courses.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.courses.data.model.CoursesPage

interface MyCoursesRepository {

    fun getMyCourses(): Flow<Resource<CoursesPage>>

}
