package org.kts.tazmin.feature.courses.domain.entity

sealed class CatalogSection {
    abstract val id: Int
    abstract val title: String

    data class CourseList(
        override val id: Int,
        override val title: String,
        val courses: List<Course>
    ) : CatalogSection()

    data class Banner(
        override val id: Int,
        override val title: String,
        val cover: String?,
        val url: String
    ) : CatalogSection()
}
