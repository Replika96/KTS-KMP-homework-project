package org.kts.tazmin.feature.courses.presentation.state

import org.kts.tazmin.feature.courses.domain.entity.Course

data class SearchUiState(
    val query: String = "",
    val results: List<Course> = emptyList(),
    val currentPage: Int = 1,
    val hasNext: Boolean = false,
    val isSearching: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isFromCache: Boolean = false,
    val cachedInfoMessage: String? = null,
    val error: String? = null,
    val showCachedBanner: Boolean = false
)
