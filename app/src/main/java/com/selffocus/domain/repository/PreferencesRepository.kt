package com.selffocus.domain.repository

/**
 * Repository interface for preferences and settings operations.
 */
interface PreferencesRepository {

    /**
     * Gets the default daily limit in minutes.
     * @return Default limit in minutes (default: 120)
     */
    suspend fun getDefaultDailyLimit(): Int

    /**
     * Sets the default daily limit.
     * @param minutes Limit in minutes
     */
    suspend fun setDefaultDailyLimit(minutes: Int)

    /**
     * Gets focus mode enabled state.
     * @return true if focus mode is enabled
     */
    suspend fun isFocusModeEnabled(): Boolean

    /**
     * Sets focus mode enabled state.
     * @param enabled New state
     */
    suspend fun setFocusModeEnabled(enabled: Boolean)

    /**
     * Gets current streak count.
     * @return Number of consecutive days under limit
     */
    suspend fun getStreakCount(): Int

    /**
     * Sets streak count.
     * @param count New streak count
     */
    suspend fun setStreakCount(count: Int)

    /**
     * Gets last active date.
     * @return Date string in yyyy-MM-dd format
     */
    suspend fun getLastActiveDate(): String?

    /**
     * Sets last active date.
     * @param date Date string in yyyy-MM-dd format
     */
    suspend fun setLastActiveDate(date: String)

    /**
     * Gets total completed focus sessions count.
     * @return Total sessions count
     */
    suspend fun getTotalFocusSessions(): Int

    /**
     * Increments total focus sessions count.
     * @return New count
     */
    suspend fun incrementFocusSessions(): Int

    /**
     * Gets theme mode preference.
     * @return "light", "dark", or "system"
     */
    suspend fun getThemeMode(): String

    /**
     * Sets theme mode preference.
     * @param mode "light", "dark", or "system"
     */
    suspend fun setThemeMode(mode: String)

    /**
     * Checks if notifications are enabled.
     * @return true if notifications enabled
     */
    suspend fun areNotificationsEnabled(): Boolean

    /**
     * Sets notifications enabled state.
     * @param enabled New state
     */
    suspend fun setNotificationsEnabled(enabled: Boolean)

    /**
     * Clears all preferences (factory reset).
     */
    suspend fun clearAllPreferences()

    /**
     * Exports preferences to JSON string.
     * @return JSON string with all preferences
     */
    suspend fun exportPreferences(): Result<String>

    /**
     * Imports preferences from JSON string.
     * @param json JSON string with preferences
     * @return Unit or error
     */
    suspend fun importPreferences(json: String): Result<Unit>
}
