package org.kts.tazmin.feature.catalog.domain.mapper

import org.kts.tazmin.feature.catalog.data.local.CatalogSectionEntity
import org.kts.tazmin.feature.catalog.data.local.CatalogSectionItemEntity
import org.kts.tazmin.feature.catalog.data.local.SectionType
import org.kts.tazmin.feature.catalog.data.model.CatalogBlockDto

interface CatalogMapper {

    fun toSectionEntity(
        block: CatalogBlockDto,
        position: Int
    ): CatalogSectionEntity

    fun toSectionItems(
        block: CatalogBlockDto,
        sectionPosition: Int
    ): List<CatalogSectionItemEntity>

    fun mapKind(kind: String): SectionType
}
