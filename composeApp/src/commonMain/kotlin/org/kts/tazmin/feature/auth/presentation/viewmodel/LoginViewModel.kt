package org.kts.tazmin.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.feature.auth.domain.usecase.LoginUseCase
import org.kts.tazmin.feature.auth.domain.validation.LoginValidator
import org.kts.tazmin.feature.auth.presentation.state.LoginError
import org.kts.tazmin.feature.auth.presentation.state.LoginUiEvent
import org.kts.tazmin.feature.auth.presentation.state.LoginUiState

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>(replay = 1)
    val events: SharedFlow<LoginUiEvent> = _events.asSharedFlow()

    fun onUsernameChanged(newUsername: String) {
        val validationResult = LoginValidator.validateUsername(newUsername)
        _state.update { currentState ->
            currentState.copy(
                username = newUsername,
                error = validationResult.toError(),
                isLoginButtonActive = LoginValidator.isFormValid(
                    username = newUsername,
                    password = currentState.password
                )
            )
        }
    }

    fun onPasswordChanged(newPassword: String) {
        val validationResult = LoginValidator.validatePassword(newPassword)
        _state.update { currentState ->
            currentState.copy(
                password = newPassword,
                error = validationResult.toError(),
                isLoginButtonActive = LoginValidator.isFormValid(
                    username = currentState.username,
                    password = newPassword
                )
            )
        }
    }

    fun onLoginClick() {
        val current = _state.value

        viewModelScope.launch {
            val result = loginUseCase(current.username, current.password)

            result.fold(
                onSuccess = {
                    _events.emit(LoginUiEvent.LoginSuccessEvent)
                },
                onFailure = {
                    _state.update {
                        it.copy(error = LoginError.Server(message = "Ошибка авторизации")) // вынести в строки
                    }
                }
            )
        }
    }
}
