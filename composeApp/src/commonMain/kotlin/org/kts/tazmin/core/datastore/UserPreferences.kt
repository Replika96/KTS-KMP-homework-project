package org.kts.tazmin.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PreferencesKeys {
    val ONBOARDING_SHOWN = booleanPreferencesKey("onboarding_shown")
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
}

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {

    val onboardingShown: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_SHOWN] ?: false
        }

    suspend fun setOnboardingShown() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_SHOWN] = true
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    val darkModeEnabled: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false
        }
}