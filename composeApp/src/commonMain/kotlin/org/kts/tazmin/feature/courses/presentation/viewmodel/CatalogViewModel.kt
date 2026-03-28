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
        initialLoad()
    }

    private fun observeCatalog() {
        getCatalogUseCase()
            .onEach { catalog ->
                _state.update { current ->
                    current.copy(
                        catalog = catalog,
                        isLoading = false,
                        isRefreshing = false,
                        catalogError = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun initialLoad() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                getCatalogUseCase.refreshFromNetwork()
            } catch (e: Throwable) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        catalogError = e.message
                    )
                }
            }
        }
    }

    fun refresh() {
        if (state.value.isRefreshing) return

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            try {
                getCatalogUseCase.refreshFromNetwork()
            } catch (e: Throwable) {
                _state.update {
                    it.copy(
                        isRefreshing = false,
                        catalogError = e.message
                    )
                }
            }
        }
    }
}
