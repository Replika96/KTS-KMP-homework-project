package org.kts.tazmin.feature.courses.data.mapper

import org.kts.tazmin.feature.courses.data.local.CatalogSectionEntity
import org.kts.tazmin.feature.courses.data.local.CatalogSectionItemEntity
import org.kts.tazmin.feature.courses.data.local.SectionType
import org.kts.tazmin.feature.courses.data.model.CatalogBlockDto

object CatalogMapper {

    fun toSectionEntity(
        dto: CatalogBlockDto,
        position: Int
    ): CatalogSectionEntity {
        return CatalogSectionEntity(
            id = dto.id,
            title = dto.title,
            type = mapKind(dto.kind),
            position = position,
            cover = dto.cover ?: dto.mobileCover,
            url = dto.detailsUrl
        )
    }

    fun toSectionItems(
        block: CatalogBlockDto,
        sectionPosition: Int
    ): List<CatalogSectionItemEntity> {

        return block.content.flatMap { content ->
            content.courses.mapIndexed { index, courseId ->
                CatalogSectionItemEntity(
                    sectionId = block.id,
                    sectionPosition = sectionPosition,
                    itemId = courseId,
                    itemType = "course",
                    position = index
                )
            }
        }
    }

    fun mapKind(kind: String): SectionType {
        return when (kind) {
            "full_course_lists" -> SectionType.FULL_COURSE_LISTS
            "simple_course_lists" -> SectionType.SIMPLE_COURSE_LISTS
            "banner" -> SectionType.BANNER
            else -> throw IllegalArgumentException("Unknown section type: $kind")
        }
    }
}
