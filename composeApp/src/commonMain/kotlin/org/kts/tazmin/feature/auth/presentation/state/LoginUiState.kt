package org.kts.tazmin.feature.auth.presentation.state

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoginButtonActive: Boolean = false,
    val error: LoginError? = null
)
