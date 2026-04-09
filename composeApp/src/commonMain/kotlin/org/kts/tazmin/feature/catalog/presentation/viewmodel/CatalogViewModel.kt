package org.kts.tazmin.feature.catalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.feature.catalog.domain.usecase.GetCatalogUseCase
import org.kts.tazmin.feature.catalog.presentation.state.CatalogUiState

class CatalogViewModel(
    private val getCatalogUseCase: GetCatalogUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    init {
        observeCatalog()
        initialLoad()
    }
    // todo ошибка вроде не обрабатывается
    private fun observeCatalog() {
        getCatalogUseCase()
            .onEach { catalog ->
                _state.update { current ->
                    current.copy(
                        catalog = catalog,
                        catalogError = null,
                        hasLoadedOnce = current.hasLoadedOnce || catalog.isNotEmpty()
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    private fun initialLoad() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, catalogError = null) }

            getCatalogUseCase.refreshFromNetwork()
                .onFailure { e ->
                    _state.update { it.copy(catalogError = (e as AppError)) }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        if (state.value.isRefreshing) return

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, catalogError = null) }

            getCatalogUseCase.refreshFromNetwork()
                .onFailure { e ->
                    _state.update { it.copy(catalogError = (e as AppError)) }
                }

            _state.update { it.copy(isRefreshing = false) }
        }
    }
}
