package org.kts.tazmin.feature.auth.presentation.state

data class OAuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val showWebView: Boolean = false,
    val error: String? = null
)
