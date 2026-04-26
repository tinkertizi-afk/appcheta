package com.selffocus.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.selffocus.core.constants.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for app preferences using DataStore.
 */
@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // Preference keys
    private val KEY_FOCUS_MODE_ENABLED = booleanPreferencesKey(AppConstants.KEY_FOCUS_MODE_ENABLED)
    private val KEY_DAILY_LIMIT_DEFAULT = intPreferencesKey(AppConstants.KEY_DAILY_LIMIT_DEFAULT_MINUTES)
    private val KEY_STREAK_COUNT = intPreferencesKey(AppConstants.KEY_STREAK_COUNT)
    private val KEY_LAST_ACTIVE_DATE = stringPreferencesKey(AppConstants.KEY_LAST_ACTIVE_DATE)
    private val KEY_TOTAL_FOCUS_SESSIONS = intPreferencesKey(AppConstants.KEY_TOTAL_FOCUS_SESSIONS)
    private val KEY_THEME_MODE = stringPreferencesKey(AppConstants.KEY_THEME_MODE)

    // Focus mode preference
    val focusModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_FOCUS_MODE_ENABLED] ?: false
    }

    suspend fun setFocusModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_FOCUS_MODE_ENABLED] = enabled
        }
    }

    // Default daily limit
    val defaultDailyLimitMinutes: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_DAILY_LIMIT_DEFAULT] ?: AppConstants.DEFAULT_FOCUS_DURATION_MINUTES
    }

    suspend fun setDefaultDailyLimit(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_DAILY_LIMIT_DEFAULT] = minutes.coerceIn(
                AppConstants.MIN_LIMIT_MINUTES,
                AppConstants.MAX_LIMIT_MINUTES
            )
        }
    }

    // Streak count
    val streakCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_STREAK_COUNT] ?: 0
    }

    suspend fun setStreakCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_STREAK_COUNT] = count.coerceAtLeast(0)
        }
    }

    suspend fun incrementStreak() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_STREAK_COUNT] ?: 0
            preferences[KEY_STREAK_COUNT] = current + 1
        }
    }

    suspend fun resetStreak() {
        dataStore.edit { preferences ->
            preferences[KEY_STREAK_COUNT] = 0
        }
    }

    // Last active date
    val lastActiveDate: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_LAST_ACTIVE_DATE]
    }

    suspend fun setLastActiveDate(date: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_ACTIVE_DATE] = date
        }
    }

    // Total focus sessions
    val totalFocusSessions: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_TOTAL_FOCUS_SESSIONS] ?: 0
    }

    suspend fun setTotalFocusSessions(count: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_TOTAL_FOCUS_SESSIONS] = count.coerceAtLeast(0)
        }
    }

    suspend fun incrementTotalFocusSessions() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_TOTAL_FOCUS_SESSIONS] ?: 0
            preferences[KEY_TOTAL_FOCUS_SESSIONS] = current + 1
        }
    }

    // Theme mode (LIGHT, DARK, SYSTEM)
    val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: "SYSTEM"
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    // Clear all preferences
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
