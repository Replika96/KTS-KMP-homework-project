package org.kts.tazmin.feature.courses.presentation.state

import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.data.model.CoursesPage

sealed class CoursesResult {
    data class Success(
        val data: CoursesPage,
        val source: Source
    ) : CoursesResult()

    data class Error(
        val message: String,
        val cachedData: CoursesPage? = null
    ) : CoursesResult(){
        val hasCachedData: Boolean = cachedData != null
    }
}
