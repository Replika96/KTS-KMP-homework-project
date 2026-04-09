package org.kts.tazmin.feature.catalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.catalog.data.model.CoursesPage
import org.kts.tazmin.feature.catalog.domain.usecase.SearchCoursesUseCase
import org.kts.tazmin.feature.catalog.presentation.state.SearchUiState

class SearchCoursesViewModel(
    private val searchCoursesUseCase: SearchCoursesUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        observeQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    when {
                        query.length >= 2 -> startSearch(query)
                        query.isBlank() -> clear()
                        else -> _state.update {
                            it.copy(results = emptyList(), isSearching = false)
                        }
                    }
                }
        }
    }

    fun onQueryChanged(q: String) {
        queryFlow.value = q
        _state.update { it.copy(query = q) }
    }

    private fun startSearch(query: String) {
        _state.update {
            it.copy(
                results = emptyList(),
                currentPage = 1,
                isSearching = true,
                error = null
            )
        }

        loadMoreJob?.cancel()

        loadMoreJob = viewModelScope.launch {
            when (val result = searchCoursesUseCase(query, 1)) {
                is Resource.Success -> handleSuccess(result.data, result.source, true)
                is Resource.Error -> handleError(result.message, result.data)
                else -> Unit
            }
        }
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoadingMore || current.isSearching || !current.hasNext) return

        loadMoreJob?.cancel()

        _state.update { it.copy(isLoadingMore = true) }

        loadMoreJob = viewModelScope.launch {
            when (val result = searchCoursesUseCase(current.query, current.currentPage + 1)) {
                is Resource.Success -> handleSuccess(result.data, result.source, false)
                is Resource.Error -> handleError(result.message, result.data)
                else -> Unit
            }
        }
    }

    private fun handleSuccess(page: CoursesPage, source: Source, first: Boolean) {
        val isFromCache = source == Source.CACHE

        _state.update { current ->
            current.copy(
                results = if (first) page.courses else current.results + page.courses,
                currentPage = page.page,
                hasNext = page.hasNext,
                isSearching = false,
                isLoadingMore = false,
                isFromCache = isFromCache,
                showCachedBanner = isFromCache && !first,
                error = null
            )
        }
    }

    private fun handleError(message: AppError, cached: CoursesPage?) {
        if (cached != null) {
            _state.update { current ->
                current.copy(
                    results = if (cached.page == 1) cached.courses else current.results + cached.courses,
                    currentPage = cached.page,
                    hasNext = cached.hasNext,
                    isSearching = false,
                    isLoadingMore = false,
                    isFromCache = true,
                    showCachedBanner = true,
                    error = message
                )
            }
        } else {
            _state.update { current ->
                current.copy(
                    error = message,
                    isSearching = false,
                    isLoadingMore = false,
                    results = if (current.currentPage == 1) emptyList() else current.results
                )
            }
        }
    }

    fun clear() {
        _state.value = SearchUiState()
    }
}
