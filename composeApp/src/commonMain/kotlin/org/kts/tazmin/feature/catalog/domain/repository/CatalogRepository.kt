package org.kts.tazmin.feature.catalog.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.catalog.domain.entity.CatalogSection

interface CatalogRepository {
    fun observeCatalog(): Flow<List<CatalogSection>>
    suspend fun refresh(): Result<Unit>
}
