package org.kts.tazmin.feature.catalog.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatalogBlocksResponse(
    @SerialName("meta")
    val meta: Meta,

    @SerialName("catalog-blocks")
    val blocks: List<CatalogBlockDto>
)

@Serializable
data class CatalogBlockDto(
    val id: Int,
    val position: Int,
    val title: String,
    val description: String,
    @SerialName("details_title")
    val detailsTitle: String,
    @SerialName("details_url")
    val detailsUrl: String,
    val cover: String? = null,
    @SerialName("mobile_cover")
    val mobileCover: String? = null,
    val language: String,
    val platform: Int,
    val kind: String,
    val appearance: String,
    @SerialName("is_title_visible")
    val isTitleVisible: Boolean,
    val content: List<CourseListDto> = emptyList()
)
@Serializable
data class CourseListDto(
    val id: Int,
    val title: String,
    val description: String = "",
    val courses: List<Int> = emptyList(),
    @SerialName("courses_count")
    val coursesCount: Int = 0
)
@Serializable
data class CatalogContentDto(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val courses: List<Int>? = null,
    @SerialName("courses_count")
    val coursesCount: Int? = null
)
