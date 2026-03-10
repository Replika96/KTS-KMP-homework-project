package org.kts.tazmin.feature.auth.domain.model

sealed class AuthResult {
    object Success : AuthResult()
    data class SuccessWithUser(val user: User) : AuthResult()
    object Cancelled : AuthResult()
    data class Error(val message: String) : AuthResult()
    object NetworkError : AuthResult()
}