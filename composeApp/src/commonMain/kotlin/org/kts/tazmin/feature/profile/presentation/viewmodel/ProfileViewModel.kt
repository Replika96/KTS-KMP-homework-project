package org.kts.tazmin.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.profile.domain.usecase.GetUserUseCase
import org.kts.tazmin.feature.profile.domain.usecase.LogoutUseCase
import org.kts.tazmin.feature.profile.presentation.state.ProfileUiState

class ProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            getUserUseCase().collect { resource ->

                _state.value = when (resource) {

                    is Resource.Loading -> {
                        ProfileUiState.Loading
                    }

                    is Resource.Success -> {
                        ProfileUiState.Success(
                            user = resource.data,
                            isFromCache = resource.source == Source.CACHE,
                            isRefreshing = resource.source == Source.CACHE,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        if (resource.data != null) {
                            // есть кэш, то показываем его и ошибку
                            ProfileUiState.Success(
                                user = resource.data,
                                isFromCache = true,
                                isRefreshing = false,
                                error = resource.message
                            )
                        } else {
                            // вообще ничего нет
                            ProfileUiState.Error(resource.message)
                        }
                    }
                }
            }
        }
    }

    fun refreshProfile() {
        loadProfile()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().fold(
                onSuccess = {
                    _state.value = ProfileUiState.LogoutSuccess
                },
                onFailure = { error ->
                    _state.value = ProfileUiState.Error(
                        message = "Ошибка выхода: ${error.message}"
                    )
                }
            )
        }
    }

    fun clearError() {
        val currentState = _state.value
        if (currentState is ProfileUiState.Success) {
            _state.value = currentState.copy(error = null)
        }
    }
}
