package org.kts.tazmin.feature.courses.presentation.state

sealed class CoursesUiEvent {
    object LoadCourses : CoursesUiEvent()
    object LoadMoreCourses : CoursesUiEvent()
    object LoadMoreSearchResults : CoursesUiEvent()
    object RefreshCourses : CoursesUiEvent()
    data class SearchQueryChanged(val query: String) : CoursesUiEvent()
    object ClearSearch : CoursesUiEvent()
}
