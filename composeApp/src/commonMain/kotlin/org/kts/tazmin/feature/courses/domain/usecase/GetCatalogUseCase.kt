package org.kts.tazmin.feature.courses.domain.usecase

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.courses.domain.entity.CatalogSection
import org.kts.tazmin.feature.courses.domain.repository.CatalogRepository

class GetCatalogUseCase(
    private val catalogRepository: CatalogRepository
) {
    operator fun invoke(): Flow<Resource<List<CatalogSection>>>{
        return catalogRepository.loadCatalog()
    }

    suspend fun refreshCatalog() {
        Napier.d("Manual refresh triggered")
        catalogRepository.refreshFromNetwork()
    }
}
