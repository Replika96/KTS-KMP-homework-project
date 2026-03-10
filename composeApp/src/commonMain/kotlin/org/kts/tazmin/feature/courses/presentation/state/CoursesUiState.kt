package org.kts.tazmin.feature.courses.presentation.state

import org.kts.tazmin.feature.courses.domain.entity.Course

data class CoursesUiState(
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val page: Int = 1,
    val hasNext: Boolean = true,
    val coursesError: String? = null,

    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Course> = emptyList()
){
    //val displayedCourses: List<Course>
    //    get() = if (searchQuery.isBlank()) courses else searchResults
}

