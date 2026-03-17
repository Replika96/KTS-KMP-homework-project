package org.kts.tazmin.feature.auth.domain.validation

import org.kts.tazmin.feature.auth.presentation.state.LoginError

object LoginValidator {

    fun validateUsername(username: String): UsernameValidationResult {
        return when {
            username.isBlank() -> UsernameValidationResult.Empty
            username.length < 3 -> UsernameValidationResult.TooShort
            !username.matches(Regex("^[a-zA-Z0-9]+$")) -> UsernameValidationResult.InvalidCharacters
            else -> UsernameValidationResult.Valid
        }
    }

    fun validatePassword(password: String): PasswordValidationResult {
        return when {
            password.isBlank() -> PasswordValidationResult.Empty
            password.length < 6 -> PasswordValidationResult.TooShort
            else -> PasswordValidationResult.Valid
        }
    }

    fun isFormValid(username: String, password: String): Boolean {
        return validateUsername(username) == UsernameValidationResult.Valid &&
                validatePassword(password) == PasswordValidationResult.Valid
    }

    sealed class UsernameValidationResult {
        object Valid : UsernameValidationResult()
        object Empty : UsernameValidationResult()
        object TooShort : UsernameValidationResult()
        object InvalidCharacters : UsernameValidationResult()

        fun toError(): LoginError? {
            return when (this) {
                Valid -> null
                Empty -> null
                TooShort -> LoginError.InvalidUserName
                InvalidCharacters -> LoginError.InvalidUserName
            }
        }
    }

    sealed class PasswordValidationResult {
        object Valid : PasswordValidationResult()
        object Empty : PasswordValidationResult()
        object TooShort : PasswordValidationResult()

        fun toError(): LoginError? {
            return when (this) {
                Valid -> null
                Empty -> null
                TooShort -> LoginError.InvalidPassword
            }
        }
    }
}
