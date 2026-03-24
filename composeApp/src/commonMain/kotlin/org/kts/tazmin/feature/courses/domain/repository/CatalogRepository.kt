package org.kts.tazmin.feature.courses.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.courses.domain.entity.CatalogSection

interface CatalogRepository {
    fun loadCatalog(): Flow<Resource<List<CatalogSection>>>

    suspend fun refreshFromNetwork()
}
