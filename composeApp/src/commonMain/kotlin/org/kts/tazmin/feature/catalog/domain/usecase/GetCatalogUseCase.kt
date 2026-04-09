package org.kts.tazmin.feature.catalog.domain.usecase

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.catalog.domain.entity.CatalogSection
import org.kts.tazmin.feature.catalog.domain.repository.CatalogRepository

class GetCatalogUseCase(
    private val catalogRepository: CatalogRepository
) {
    operator fun invoke(): Flow<List<CatalogSection>> =
        catalogRepository.observeCatalog()

    suspend fun refreshFromNetwork(): Result<Unit> {
        Napier.d("Manual refresh triggered")
        return catalogRepository.refresh()
    }
}
