package org.kts.tazmin.feature.profile.presentation.state

import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.feature.profile.domain.model.Profile

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object LogoutSuccess : ProfileUiState()

    data class Success(
        val user: Profile,
        val isRefreshing: Boolean = false,
        val error: AppError? = null,
        val isFromCache: Boolean = false
    ) : ProfileUiState()

    data class Error(
        val message: AppError
    ) : ProfileUiState()
}
