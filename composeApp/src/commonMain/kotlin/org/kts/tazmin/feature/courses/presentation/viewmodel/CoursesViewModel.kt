package org.kts.tazmin.feature.courses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.domain.usecase.GetCoursesUseCase
import org.kts.tazmin.feature.courses.domain.usecase.SearchCoursesUseCase
import org.kts.tazmin.feature.courses.presentation.state.CoursesResult
import org.kts.tazmin.feature.courses.presentation.state.CoursesUiEvent
import org.kts.tazmin.feature.courses.presentation.state.CoursesUiState
import org.kts.tazmin.feature.courses.presentation.state.SearchUiState

class CoursesViewModel(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val searchCoursesUseCase: SearchCoursesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoursesUiState())
    val state: StateFlow<CoursesUiState> = _state.asStateFlow()

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        Napier.d("CoursesViewModel init")
        observeSearchQuery()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // у степика странно, что курсы грузят иногда то только на второй странице, то ток на первой
            // https://stepik.org/api/courses?page=2
            when (val result = getCoursesUseCase(page = 1)) {

                is CoursesResult.Success -> {
                    handleSuccessResult(result)
                }

                is CoursesResult.Error -> {
                    handleErrorResult(result)
                }
            }
        }
    }

    private fun handleSuccessResult(result: CoursesResult.Success) {
        val page = result.data
        val isFromCache = result.source == Source.CACHE

        Napier.d("Загружено ${page.courses.size} курсов (cache=$isFromCache)")

        _state.update {
            it.copy(
                courses = page.courses,
                page = page.page,
                hasNext = page.hasNext,
                isLoading = false,
                isLoadingMore = false,
                isFromCache = isFromCache,
                cachedInfoMessage = if (isFromCache) "Показаны сохранённые данные" else null,
                coursesError = null  // Ошибки нет
            )
        }
    }

    private fun handleErrorResult(result: CoursesResult.Error) {
        Napier.e("Ошибка загрузки курсов: ${result.message}")

        if (result.hasCachedData) {
            // есть кэш и показываем его с сообщением об ошибке
            val cachedPage = result.cachedData!!
            _state.update {
                it.copy(
                    courses = cachedPage.courses,
                    page = cachedPage.page,
                    hasNext = cachedPage.hasNext,
                    isLoading = false,
                    isLoadingMore = false,
                    isFromCache = true,
                    cachedInfoMessage = "Показаны сохранённые данные",
                    coursesError = result.message  // Ошибка для банера
                )
            }
        } else {
            // нет кэша и только ошибка
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    isFromCache = false,
                    coursesError = result.message,
                    courses = emptyList()
                )
            }
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

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length >= 2) {
                        performSearch(query)
                    } else if (query.isBlank()) {
                        clearSearch()
                    }
                }
        }
    }

    private suspend fun performSearch(query: String) {
        _searchState.update { it.copy(isSearching = true, query = query, error = null) }

        when (val result = searchCoursesUseCase(query, page = 1)) {
            is CoursesResult.Success -> {
                _searchState.update {
                    it.copy(
                        results = result.data.courses,
                        isSearching = false,
                        isFromCache = result.source == Source.CACHE,
                        cachedInfoMessage = if (result.source == Source.CACHE)
                            "Показаны сохранённые данные" else null,
                        error = null
                    )
                }
            }

            is CoursesResult.Error -> {
                if (result.hasCachedData) {
                    val cachedPage = result.cachedData!!
                    _searchState.update {
                        it.copy(
                            results = cachedPage.courses,
                            isSearching = false,
                            isFromCache = true,
                            cachedInfoMessage = "Показаны сохранённые данные",
                            error = result.message
                        )
                    }
                } else {
                    _searchState.update {
                        it.copy(
                            error = result.message,
                            isSearching = false,
                            isFromCache = false,
                            results = emptyList()
                        )
                    }
                }
            }
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

    private fun clearSearch() {
        _state.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false
            )
        }
        loadCourses()
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

            when (val result = getCoursesUseCase(page = nextPage)) {

                is CoursesResult.Success -> {
                    val response = result.data

                    _state.update {
                        it.copy(
                            courses = it.courses + response.courses,
                            page = response.page,
                            hasNext = response.hasNext,
                            isLoadingMore = false
                        )
                    }
                }

                is CoursesResult.Error -> {

                    Napier.e("Ошибка загрузки следующей страницы: ${result.message}")

                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            coursesError = result.message
                        )
                    }
                }
            }
        }
    }

    private fun refreshCourses() {
        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = true,
                    page = 2,
                    coursesError = null
                )
            }

            when (val result = getCoursesUseCase(page = 2)) {

                is CoursesResult.Success -> {
                    val page = result.data

                    _state.update {
                        it.copy(
                            courses = page.courses,
                            page = page.page,
                            hasNext = page.hasNext,
                            isLoading = false
                        )
                    }
                }

                is CoursesResult.Error -> {

                    Napier.e("Ошибка обновления: ${result.message}")

                    _state.update {
                        it.copy(
                            isLoading = false,
                            coursesError = result.message
                        )
                    }
                }
            }
        }
    }
    fun clearError() {
        _state.update { it.copy(coursesError = null) }
    }

    fun clearSearchError() {
        _searchState.update { it.copy(error = null) }
    }
}
