package org.kts.tazmin.feature.courses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.courses.domain.entity.CatalogSection
import org.kts.tazmin.feature.courses.domain.usecase.GetCatalogUseCase
import org.kts.tazmin.feature.courses.presentation.state.CatalogUiState

class CatalogViewModel(
    private val getCatalogUseCase: GetCatalogUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    init {
        observeCatalog()
        refresh()
    }

    private fun observeCatalog() {
        getCatalogUseCase()
            .onEach { handleResource(it) }
            .launchIn(viewModelScope)
    }

    private fun handleResource(resource: Resource<List<CatalogSection>>) {
        when (resource) {

            is Resource.Loading -> {
                _state.update {
                    it.copy(
                        isLoading = it.catalog.isEmpty(),
                        isRefreshing = it.catalog.isNotEmpty()
                    )
                }
            }

            is Resource.Success -> {
                _state.update { current ->

                    // если данные одинаковы, то не триггерим лишний recomposition
                    if (current.catalog == resource.data &&
                        current.isFromCache == (resource.source == Source.CACHE)
                    ) return

                    current.copy(
                        catalog = resource.data,
                        isLoading = false,
                        isRefreshing = false,
                        isFromCache = resource.source == Source.CACHE,
                        catalogError = null
                    )
                }
            }

            is Resource.Error -> {
                _state.update {
                    it.copy(
                        catalog = resource.data ?: emptyList(),
                        isLoading = false,
                        isRefreshing = false,
                        catalogError = resource.message,
                        isFromCache = resource.data != null,
                    )
                }
            }
        }
    }

    fun refresh() {
        if (state.value.isRefreshing) return

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            getCatalogUseCase.refreshCatalog()
        }
    }
}
