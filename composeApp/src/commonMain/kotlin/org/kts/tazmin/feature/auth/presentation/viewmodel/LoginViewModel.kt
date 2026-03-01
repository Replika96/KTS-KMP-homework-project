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
import org.kts.tazmin.feature.auth.presentation.state.LoginError
import org.kts.tazmin.feature.auth.presentation.state.LoginUiEvent
import org.kts.tazmin.feature.auth.presentation.state.LoginUiState

class LoginViewModel(private val loginUseCase: LoginUseCase): ViewModel() {

    private val _state= MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>(replay = 1)
    val events: SharedFlow<LoginUiEvent> = _events.asSharedFlow()
    fun onUsernameChanged(newUsername: String){
        _state.update { currentState ->
            currentState.copy(
                username = newUsername,
                isLoginButtonActive = validateForm(
                    username = newUsername,
                    password = currentState.password
                ),
                error = when{
                    newUsername.isBlank() -> null
                    newUsername.length < 3 -> LoginError.InvalidUserName
                    else -> null
                }
            )
        }
    }
    fun onPasswordChanged(newPassword: String){
        _state.update { currentState ->
            currentState.copy(
                password = newPassword,
                isLoginButtonActive = validateForm(
                    username = currentState.username,
                    password = newPassword
                ),
                error = when {
                    newPassword.isBlank() -> null
                    newPassword.length < 6 -> LoginError.InvalidPassword
                    else -> null
                }
            )
        }
    }

    fun onLoginCLick(){
        val current = _state.value


        viewModelScope.launch {
            val result = loginUseCase(current.username, current.password)

            result.fold(
                onSuccess = {
                    _events.emit(LoginUiEvent.LoginSuccessEvent)
                },
                onFailure = {
                    _state.update {
                        it.copy(error = LoginError.Server("Ошибка авторизации"))
                    }
                }
            )
        }
    }
    private fun validateForm(username: String, password: String): Boolean {
        return username.length >= 3 && password.length >= 6
    }
}
