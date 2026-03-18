package org.kts.tazmin.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.kts.tazmin.core.datastore.UserPreferences
import org.kts.tazmin.core.token.TokenStorage
import org.kts.tazmin.navigation.Screen

class AppStartViewModel(
    private val preferences: UserPreferences,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Screen?>(null)
    val startDestination: StateFlow<Screen?> = _startDestination

    init {
        viewModelScope.launch {
            combine(
                preferences.onboardingShown,
                flow { emit(tokenStorage.isLoggedIn()) }
            ) { onboardingShown, loggedIn ->
                if (!onboardingShown) {
                    Screen.Onboarding
                } else {
                    if (loggedIn) Screen.Courses else Screen.Onboarding
                }
            }.collect { screen ->
                _startDestination.value = screen
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingShown()
            Napier.e(tag = "AppStartViewModel", message = "Onboarding пройден")
        }
    }
}
