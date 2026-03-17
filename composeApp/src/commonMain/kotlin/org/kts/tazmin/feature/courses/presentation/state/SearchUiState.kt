package org.kts.tazmin.feature.courses.presentation.state

import org.kts.tazmin.feature.courses.domain.entity.Course

data class SearchUiState(
    val query: String = "",
    val results: List<Course> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
    val isFromCache: Boolean = false,
    val cachedInfoMessage: String? = null
)
