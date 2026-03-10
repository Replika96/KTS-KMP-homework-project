package org.kts.tazmin.feature.courses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.feature.courses.domain.usacase.GetCoursesUseCase
import org.kts.tazmin.feature.courses.domain.usacase.SearchCoursesUseCase
import org.kts.tazmin.feature.courses.presentation.state.CoursesUiEvent
import org.kts.tazmin.feature.courses.presentation.state.CoursesUiState

class CoursesViewModel(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val searchCoursesUseCase: SearchCoursesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoursesUiState())
    val state: StateFlow<CoursesUiState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        loadCourses()
        setupSearch()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getCoursesUseCase(
                page = 1,
            )
            result.fold(
                onSuccess = { page ->
                    Napier.d("Загружено ${page.courses.size} курсов")
                    _state.update {
                        it.copy(
                            courses = page.courses,
                            page = page.page,
                            hasNext = page.hasNext,
                            isLoading = false,
                            coursesError = null
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

    fun handleEvent(event: CoursesUiEvent) {
        when (event) {
            is CoursesUiEvent.LoadCourses -> loadCourses()
            is CoursesUiEvent.LoadMoreCourses -> loadMoreCourses()
            is CoursesUiEvent.RefreshCourses -> refreshCourses()
            is CoursesUiEvent.SearchQueryChanged -> onSearchQueryChanged(event.query)
            CoursesUiEvent.ClearSearch -> clearSearch()
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            clearSearch()
        } else {
            viewModelScope.launch {
                searchQuery.emit(query)
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun setupSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .flatMapLatest { query ->
                    _state.update { it.copy(isSearching = true) }
                    flow {
                        emit(searchCoursesUseCase(query, page = 1))
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { courses ->
                            _state.update {
                                it.copy(
                                    searchResults = courses,
                                    isSearching = false
                                )
                            }
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(
                                    coursesError = "Ошибка поиска: ${error.message}",
                                    isSearching = false
                                )
                            }
                        }
                    )
                }
        }
    }

    private fun loadMoreCourses() {

        val currentState = _state.value

        if (currentState.isLoadingMore ||
            currentState.isLoading ||
            !currentState.hasNext ||
            currentState.searchQuery.isNotBlank()
        ) {
            return
        }

        viewModelScope.launch {

            _state.update { it.copy(isLoadingMore = true) }

            val nextPage = currentState.page + 1

            val result = getCoursesUseCase(page = nextPage)

            result.fold(
                onSuccess = { response ->

                    _state.update {
                        it.copy(
                            courses = it.courses + response.courses,
                            page = response.page,
                            hasNext = response.hasNext,
                            isLoadingMore = false
                        )
                    }

                },
                onFailure = { error ->

                    Napier.e("Ошибка загрузки следующей страницы", error)

                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            coursesError = error.message
                        )
                    }

                }
            )
        }
    }

    private fun refreshCourses() {
        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = true,
                    page = 1,
                    coursesError = null
                )
            }

            val result = getCoursesUseCase(page = 1)

            result.fold(
                onSuccess = { page ->

                    _state.update {
                        it.copy(
                            courses = page.courses,
                            page = page.page,
                            hasNext = page.hasNext,
                            isLoading = false
                        )
                    }

                },
                onFailure = { error ->

                    Napier.e("Ошибка обновления", error)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            coursesError = error.message ?: "Unknown error"
                        )
                    }

                }
            )
        }
    }

    private fun clearSearch() {
        _state.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false
            )
        }
    }
}
