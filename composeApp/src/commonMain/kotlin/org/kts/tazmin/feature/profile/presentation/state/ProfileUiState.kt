package org.kts.tazmin.feature.profile.presentation.state

import org.kts.tazmin.feature.profile.domain.model.User

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object LogoutSuccess : ProfileUiState()

    data class Success(
        val user: User,
        val isRefreshing: Boolean = false,
        val error: String? = null,
        val isFromCache: Boolean = false
    ) : ProfileUiState()

    data class Error(
        val message: String
    ) : ProfileUiState()
}
