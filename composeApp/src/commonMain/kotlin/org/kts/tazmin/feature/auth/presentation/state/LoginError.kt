package org.kts.tazmin.feature.auth.presentation.state

sealed interface LoginError {
    data object InvalidUserName: LoginError
    data object InvalidPassword: LoginError
    data class Server(val message: String): LoginError
}
