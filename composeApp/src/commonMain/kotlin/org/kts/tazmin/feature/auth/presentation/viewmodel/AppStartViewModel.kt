package org.kts.tazmin.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.kts.tazmin.core.datastore.UserPreferences
import org.kts.tazmin.navigation.Screen

class AppStartViewModel(
    private val preferences: UserPreferences
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Screen?>(null)
    val startDestination: StateFlow<Screen?> = _startDestination

    init {
        viewModelScope.launch {
            preferences.onboardingShown.collect { shown ->
                _startDestination.value =
                    if (shown) Screen.Courses else Screen.Onboarding
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
