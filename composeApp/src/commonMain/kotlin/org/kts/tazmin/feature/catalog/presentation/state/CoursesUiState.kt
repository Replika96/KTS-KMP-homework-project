package org.kts.tazmin.feature.catalog.presentation.state

import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.feature.catalog.domain.entity.Course

data class CoursesUiState(
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val page: Int = 1,
    val hasNext: Boolean = true,
    val coursesError: AppError? = null,
    val isFromCache: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Course> = emptyList(),
    val isRefreshing: Boolean = false,
    val showCachedBanner: Boolean = false
)

