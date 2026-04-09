package org.kts.tazmin.feature.catalog.presentation.state

import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.feature.catalog.domain.entity.Course

data class SearchUiState(
    val query: String = "",
    val results: List<Course> = emptyList(),
    val currentPage: Int = 1,
    val hasNext: Boolean = false,
    val isSearching: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isFromCache: Boolean = false,
    val error: AppError? = null,
    val showCachedBanner: Boolean = false
)
