package org.kts.tazmin.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository
import org.kts.tazmin.feature.auth.presentation.state.OAuthState

class OAuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OAuthState())
    val state: StateFlow<OAuthState> = _state.asStateFlow()

    fun onLoginWithStepik() {
        _state.update {
            it.copy(
                showWebView = true,
                error = null
            )
        }
    }

    fun onCodeReceived(code: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showWebView = false) }

            val result = authRepository.login(code)

            result.fold(
                onSuccess = { user ->
                    Napier.d("OAuth успешен: $user")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true
                        )
                    }
                },
                onFailure = { error ->
                    Napier.e("OAuth ошибка", error)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Ошибка авторизации"
                        )
                    }
                }
            )
        }
    }

    fun onError(message: String) {
        _state.update {
            it.copy(
                isLoading = false,
                showWebView = false,
                error = message
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetWebView() {
        _state.update { it.copy(showWebView = false) }
    }
}
