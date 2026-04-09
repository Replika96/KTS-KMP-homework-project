package org.kts.tazmin.feature.catalog.presentation.state

import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.feature.catalog.domain.entity.CatalogSection

data class CatalogUiState(
    val catalog: List<CatalogSection> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val catalogError: AppError? = null,
    val isFromCache: Boolean = false,
    val hasLoadedOnce: Boolean = false
)
