package org.kts.tazmin.feature.auth.presentation.state

sealed interface LoginUiEvent {
    data object LoginSuccessEvent: LoginUiEvent
}
