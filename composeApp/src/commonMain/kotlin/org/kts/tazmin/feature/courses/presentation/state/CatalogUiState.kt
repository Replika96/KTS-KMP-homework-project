package org.kts.tazmin.feature.courses.presentation.state

import org.kts.tazmin.feature.courses.domain.entity.CatalogSection

data class CatalogUiState(
    val catalog: List<CatalogSection> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val catalogError: String? = null,
    val isFromCache: Boolean = false
)
