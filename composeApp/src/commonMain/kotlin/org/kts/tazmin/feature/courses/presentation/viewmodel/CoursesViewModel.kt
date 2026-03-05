package org.kts.tazmin.feature.courses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.feature.courses.domain.usacase.GetCoursesUseCase
//import org.kts.tazmin.feature.feed.presentation.state.CoursesUiEvent
import org.kts.tazmin.feature.courses.presentation.state.CoursesUiState

class CoursesViewModel(private val getCoursesUseCase: GetCoursesUseCase): ViewModel() {
    private val _state = MutableStateFlow(CoursesUiState())
    val state: StateFlow<CoursesUiState> = _state.asStateFlow()

    /*
    fun handleEvent(event: CoursesUiEvent) {
        when (event) {
            is CoursesUiEvent.LoadCourses -> loadCourses()
            is CoursesUiEvent.LoadMoreCourses -> loadMoreCourses()
            is CoursesUiEvent.RefreshCourses -> refreshCourses()
            is CoursesUiEvent.SearchQueryChanged -> search(event.query)
            CoursesUiEvent.ClearSearch -> clearSearch()
        }
    }
     */
     fun loadCourses(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getCoursesUseCase(
                page = 1,
                limit = 20
            )
            result.fold(
                onSuccess = { coursesResponse ->
                    Napier.d("Загружено ${coursesResponse.courses.size} курсов")
                    _state.update {
                        it.copy(
                            courses = coursesResponse.courses,
                            page = coursesResponse.meta.page,
                            isLoading = false
                        )
                    }
                },
                onFailure = { throwable ->
                    Napier.e("Ошибка", throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            coursesError = throwable.message ?: "Unkown error"
                        )
                    }
                }
            )
        }
    }
    // fun loadCourses()
}
