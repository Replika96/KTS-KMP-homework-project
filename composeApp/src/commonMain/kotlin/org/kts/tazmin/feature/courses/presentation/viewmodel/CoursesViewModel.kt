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
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.data.model.CoursesPage
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

    private var catalogJob: Job? = null
    private var searchJob: Job? = null
    private var loadMoreCatalogJob: Job? = null
    private var loadMoreSearchJob: Job? = null

    init {
        observeCourses()
        observeSearchQuery()
    }

    // курсы
    private fun observeCourses() {
        catalogJob?.cancel()

        catalogJob = viewModelScope.launch {
            getCoursesUseCase(page = 1, pageSize = 20)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = true,
                                    coursesError = null,
                                    cachedInfoMessage = null
                                )
                            }
                        }

                        is Resource.Success -> {
                            handleCoursesSuccess(
                                page = resource.data,
                                source = resource.source,
                                isFirstPage = true
                            )
                        }

                        is Resource.Error -> {
                            handleCoursesError(
                                message = resource.message,
                                cachedData = resource.data
                            )
                        }
                    }
                }
        }
    }

    private fun handleCoursesSuccess(
        page: CoursesPage,
        source: Source,
        isFirstPage: Boolean
    ) {
        val isFromCache = source == Source.CACHE

        val shouldShowBanner = isFromCache && (
                !isFirstPage || // это подгрузка следующих страниц
                        _state.value.courses.isNotEmpty() // уже были данные
                )

        Napier.d("Каталог: загружено ${page.courses.size} курсов (cache=$isFromCache, page=${page.page})")

        _state.update { current ->
            current.copy(
                courses = if (isFirstPage) page.courses else current.courses + page.courses,
                page = page.page,
                hasNext = page.hasNext,
                isLoading = false,
                isLoadingMore = false,
                isRefreshing = false,
                isFromCache = isFromCache,
                cachedInfoMessage = if (isFromCache) "Показаны сохранённые данные" else null,
                coursesError = null,
                showCachedBanner = shouldShowBanner,
            )
        }
    }

    private fun handleCoursesError(
        message: String,
        cachedData: CoursesPage?
    ) {
        Napier.e("Каталог: ошибка загрузки курсов: $message")

        if (cachedData != null) {
            _state.update {
                it.copy(
                    courses = cachedData.courses,
                    page = cachedData.page,
                    hasNext = cachedData.hasNext,
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    showCachedBanner = true,
                    isFromCache = true,
                    cachedInfoMessage = "Показаны сохранённые данные",
                    coursesError = message
                )
            }
        } else {
            _state.update {
                it.copy(
                    courses = emptyList(),
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    isFromCache = false,
                    cachedInfoMessage = null,
                    coursesError = message
                )
            }
        }
    }

    // поиск
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    when {
                        query.length >= 2 -> {
                            startSearch(query)
                        }

                        query.isBlank() -> {
                            clearSearchInternal()
                        }

                        else -> {
                            _searchState.update {
                                it.copy(
                                    results = emptyList(),
                                    isSearching = false,
                                    error = null,
                                    cachedInfoMessage = null,
                                    isFromCache = false
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun startSearch(query: String) {
        // сбрасываем состояние
        _searchState.update {
            it.copy(
                query = query,
                currentPage = 1,
                results = emptyList(),
                isSearching = true,
                isLoadingMore = false,
                error = null,
                cachedInfoMessage = null,
                isFromCache = false
            )
        }

        loadMoreSearchJob?.cancel()

        loadMoreSearchJob = viewModelScope.launch {
            when (val result = searchCoursesUseCase(query, page = 1)) {
                is CoursesResult.Success -> {
                    handleSearchSuccess(
                        page = result.data,
                        source = result.source,
                        isFirstPage = true
                    )
                }

                is CoursesResult.Error -> {
                    handleSearchError(result)
                }
            }
        }
    }


    private fun handleSearchSuccess(
        page: CoursesPage,
        source: Source,
        isFirstPage: Boolean
    ) {
        val isFromCache = source == Source.CACHE

        val shouldShowBanner = isFromCache && (
                !isFirstPage ||
                        _searchState.value.results.isNotEmpty()
                )
        Napier.d("Поиск: загружено ${page.courses.size} курсов (cache=$isFromCache, page=${page.page})")

        _searchState.update { current ->
            current.copy(
                results = if (isFirstPage) page.courses else current.results + page.courses,
                currentPage = page.page,
                hasNext = page.hasNext,
                isSearching = false,
                isLoadingMore = false,
                showCachedBanner = shouldShowBanner,
                isFromCache = isFromCache,
                cachedInfoMessage = if (isFromCache) "Показаны сохранённые данные" else null,
                error = null
            )
        }
    }

    private fun handleSearchError(result: CoursesResult.Error) {
        Napier.e("Поиск: ошибка: ${result.message}")

        if (result.hasCachedData) {
            val cachedPage = result.cachedData!!
            _searchState.update { current ->
                current.copy(
                    results = if (cachedPage.page == 1)
                        cachedPage.courses
                    else
                        current.results + cachedPage.courses,
                    currentPage = cachedPage.page,
                    hasNext = cachedPage.hasNext,
                    isSearching = false,
                    isLoadingMore = false,
                    isFromCache = true,
                    showCachedBanner = true,
                    cachedInfoMessage = "Показаны сохранённые данные",
                    error = result.message
                )
            }
        } else {
            _searchState.update { current ->
                current.copy(
                    error = result.message,
                    isSearching = false,
                    isLoadingMore = false,
                    isFromCache = false,
                    cachedInfoMessage = null,
                    results = if (current.currentPage == 1) emptyList() else current.results
                )
            }
        }
    }

    // event
    fun handleEvent(event: CoursesUiEvent) {
        when (event) {
            is CoursesUiEvent.LoadCourses -> reloadCatalog()
            is CoursesUiEvent.LoadMoreCourses -> loadMoreCatalog()
            is CoursesUiEvent.RefreshCourses -> refreshCatalog()
            is CoursesUiEvent.SearchQueryChanged -> onSearchQueryChanged(event.query)
            is CoursesUiEvent.LoadMoreSearchResults -> loadMoreSearchResults()
            CoursesUiEvent.ClearSearch -> clearSearch()
        }
    }

    private fun reloadCatalog() {
        observeCourses()
    }

    private fun refreshCatalog() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, coursesError = null) }

            when (val result = getCoursesUseCase.forceRefresh(page = 1, pageSize = 20)) {
                is CoursesResult.Success -> {
                    handleCoursesSuccess(
                        page = result.data,
                        source = result.source,
                        isFirstPage = true
                    )
                }

                is CoursesResult.Error -> {
                    handleCoursesError(result.message, result.cachedData)
                }
            }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchQuery.value = query
    }

    private fun clearSearchInternal() {
        _state.update {
            it.copy(
                searchQuery = "",
                isSearching = false
            )
        }
        _searchState.update {
            SearchUiState()
        }
    }

    fun clearSearch() {
        clearSearchInternal()
        reloadCatalog()
    }

    // пагинация
    private fun loadMoreCatalog() {
        val current = _state.value

        if (current.isLoadingMore ||
            current.isLoading ||
            current.isRefreshing ||
            !current.hasNext ||
            current.searchQuery.isNotBlank()
        ) return

        loadMoreCatalogJob?.cancel()

        _state.update { it.copy(isLoadingMore = true, coursesError = null) }

        loadMoreCatalogJob = viewModelScope.launch {
            getCoursesUseCase(page = current.page + 1, pageSize = 20)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> Unit
                        is Resource.Success -> {
                            handleCoursesSuccess(
                                page = resource.data,
                                source = resource.source,
                                isFirstPage = false
                            )
                        }

                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoadingMore = false,
                                    coursesError = resource.message
                                )
                            }
                        }
                    }
                }
        }
    }

    fun loadMoreSearchResults() {
        val current = _searchState.value

        if (current.isLoadingMore ||
            current.isSearching ||
            !current.hasNext ||
            current.query.length < 2
        ) return

        loadMoreSearchJob?.cancel()

        _searchState.update { it.copy(isLoadingMore = true, error = null) }

        loadMoreSearchJob = viewModelScope.launch {
            when (val result = searchCoursesUseCase(current.query, current.currentPage + 1)) {
                is CoursesResult.Success -> {
                    handleSearchSuccess(
                        page = result.data,
                        source = result.source,
                        isFirstPage = false
                    )
                }

                is CoursesResult.Error -> {
                    handleSearchError(result)
                }
            }
        }
    }

    // ошибки
    fun clearError() {
        _state.update { it.copy(coursesError = null) }
    }

    fun clearSearchError() {
        _searchState.update { it.copy(error = null) }
    }
}

