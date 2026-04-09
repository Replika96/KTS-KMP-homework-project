package org.kts.tazmin.feature.catalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.catalog.domain.usecase.GetMyCoursesUseCase
import org.kts.tazmin.feature.catalog.presentation.state.CoursesUiState

class MyCoursesViewModel(
    private val getMyCoursesUseCase: GetMyCoursesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoursesUiState())
    val state = _state.asStateFlow()

    private var job: Job? = null

    fun loadCourses() {
        job?.cancel()

        job = viewModelScope.launch {
            getMyCoursesUseCase().collect { resource ->
                when (resource) {

                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                courses = resource.data.courses,
                                isLoading = false,
                                isFromCache = resource.source == Source.CACHE,
                                coursesError = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                courses = resource.data?.courses ?: emptyList(),
                                isLoading = false,
                                coursesError = resource.message,
                                isFromCache = resource.data != null
                            )
                        }
                    }
                }
            }
        }
    }

    fun refresh() {
        loadCourses()
    }
}
