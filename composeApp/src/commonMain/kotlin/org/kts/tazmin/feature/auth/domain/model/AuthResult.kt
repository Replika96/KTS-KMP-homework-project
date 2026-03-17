package org.kts.tazmin.feature.auth.domain.model

import org.kts.tazmin.feature.profile.domain.model.User

sealed class AuthResult {
    object Success : AuthResult()
    data class SuccessWithUser(val user: User) : AuthResult()
    object Cancelled : AuthResult()
    data class Error(val message: String) : AuthResult()
    object NetworkError : AuthResult()
}
