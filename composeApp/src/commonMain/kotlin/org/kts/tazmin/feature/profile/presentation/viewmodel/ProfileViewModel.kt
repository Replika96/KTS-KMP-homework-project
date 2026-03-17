package org.kts.tazmin.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException
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
            _state.value = ProfileUiState.Loading

            getUserUseCase().fold(
                onSuccess = { currentUser ->
                    _state.value = ProfileUiState.Success(
                        user = currentUser,
                        isFromCache = false
                    )
                },
                onFailure = { throwable ->
                    // ошибка сети и нет кэша?
                    if (throwable is IOException && throwable.message?.contains("Нет сети и нет кэша") == true) {
                        _state.value = ProfileUiState.Error(
                            message = "Нет подключения к интернету и нет сохраненных данных"
                        )
                    } else {
                        _state.value = ProfileUiState.Error(
                            message = throwable.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            val currentState = _state.value

            if (currentState is ProfileUiState.Success) {
                // идет обновление, но старые данные остаются
                _state.value = currentState.copy(isRefreshing = true, error = null)

                getUserUseCase().fold(
                    onSuccess = { user ->
                        _state.value = ProfileUiState.Success(
                            user = user,
                        )
                    },
                    onFailure = { throwable ->
                        if (throwable is IOException) {
                            // показываем старые данные с предупреждением
                            _state.value = currentState.copy(
                                isRefreshing = false,
                                error = "Не удалось обновить данные. Показаны сохраненные данные."
                            )
                        } else {
                            _state.value = ProfileUiState.Error(
                                message = throwable.message ?: "Error"
                            )
                        }
                    }
                )
            } else {
                loadProfile()
            }
        }
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