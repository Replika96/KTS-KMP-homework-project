package org.kts.tazmin.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.core.utils.formatRelativeTime
import org.kts.tazmin.feature.profile.domain.usecase.GetUserUseCase
import org.kts.tazmin.feature.profile.domain.usecase.LogoutUseCase
import org.kts.tazmin.feature.profile.presentation.state.ProfileUiState
import kotlin.time.Instant

class ProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun loadProfile() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            getUserUseCase().collect { resource ->
                _state.value = when (resource) {
                    is Resource.Loading -> ProfileUiState.Loading

                    is Resource.Success -> {
                        val user = resource.data

                        val formattedJoinedAt = runCatchingCancellable {
                            formatRelativeTime(Instant.parse(user.joinedAt))
                        }.getOrElse { "—" }

                        ProfileUiState.Success(
                            user = user.copy(
                                joinedAtFormatted = formattedJoinedAt
                            ),
                            isFromCache = resource.source == Source.CACHE,
                            isRefreshing = resource.source == Source.CACHE,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        if (resource.data != null) {
                            ProfileUiState.Success(
                                user = resource.data,
                                isFromCache = true,
                                isRefreshing = false,
                                error = resource.message
                            )
                        } else {
                            ProfileUiState.Error(resource.message)
                        }
                    }
                }
            }
        }
    }

    fun refreshProfile() {
        loadProfile()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.value = ProfileUiState.LogoutSuccess
        }
    }

    fun clearError() {
        val currentState = _state.value
        if (currentState is ProfileUiState.Success) {
            _state.value = currentState.copy(error = null)
        }
    }
}
