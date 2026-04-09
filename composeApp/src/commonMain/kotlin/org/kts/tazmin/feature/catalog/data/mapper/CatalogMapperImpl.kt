package org.kts.tazmin.feature.catalog.data.mapper

import org.kts.tazmin.feature.catalog.data.local.CatalogSectionEntity
import org.kts.tazmin.feature.catalog.data.local.CatalogSectionItemEntity
import org.kts.tazmin.feature.catalog.data.local.SectionType
import org.kts.tazmin.feature.catalog.data.model.CatalogBlockDto
import org.kts.tazmin.feature.catalog.domain.mapper.CatalogMapper

class CatalogMapperImpl : CatalogMapper {

    override fun toSectionEntity(block: CatalogBlockDto, position: Int): CatalogSectionEntity {
        val firstList = block.content.firstOrNull()
        return CatalogSectionEntity(
            id = block.id,
            title = block.title,
            type = mapKind(block.kind),
            position = position,
            cover = block.cover,
            courseListId = firstList?.id,
            totalCount = block.content.sumOf { it.coursesCount }
        )
    }

    override fun toSectionItems(
        block: CatalogBlockDto,
        sectionPosition: Int
    ): List<CatalogSectionItemEntity> {

        if (block.kind != "full_course_lists" &&
            block.kind != "simple_course_lists" &&
            block.kind != "banner"
        ) return emptyList()

        return block.content.flatMap { courseList ->
            courseList.courses.mapIndexed { index, courseId ->
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

    override fun mapKind(kind: String): SectionType {
        return when (kind) {
            "full_course_lists" -> SectionType.FULL_COURSE_LISTS
            "simple_course_lists" -> SectionType.SIMPLE_COURSE_LISTS
            "banner" -> SectionType.BANNER
            else -> SectionType.UNKNOWN
        }
    }
}
