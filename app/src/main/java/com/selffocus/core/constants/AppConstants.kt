package com.selffocus.core.constants

/**
 * Application-wide constants for SelfFocus.
 */
object AppConstants {
    // Notification Channel IDs
    const val NOTIFICATION_CHANNEL_USAGE = "usage_tracking"
    const val NOTIFICATION_CHANNEL_FOCUS = "focus_sessions"
    const val NOTIFICATION_CHANNEL_LIMITS = "limit_alerts"

    // Notification IDs
    const val NOTIFICATION_ID_USAGE_SYNC = 1001
    const val NOTIFICATION_ID_FOCUS_SESSION = 1002
    const val NOTIFICATION_ID_LIMIT_REACHED = 1003
    const val NOTIFICATION_ID_LIMIT_WARNING = 1004

    // WorkManager
    const val WORK_NAME_STATS_SYNC = "stats_sync_work"
    const val STATS_SYNC_INTERVAL_MINUTES = 15L

    // Usage Stats
    const val USAGE_STATS_QUERY_INTERVAL_MS = 24 * 60 * 60 * 1000L // 24 hours
    const val MAX_QUERY_RANGE_MS = 7 * 24 * 60 * 60 * 1000L // 7 days

    // Focus Session
    const val DEFAULT_FOCUS_DURATION_MINUTES = 25
    const val MIN_FOCUS_DURATION_MINUTES = 1
    const val MAX_FOCUS_DURATION_MINUTES = 180

    // DataStore Keys
    const val PREFS_NAME = "selffocus_prefs"
    const val KEY_FOCUS_MODE_ENABLED = "focus_mode_enabled"
    const val KEY_DAILY_LIMIT_DEFAULT_MINUTES = "daily_limit_default_minutes"
    const val KEY_STREAK_COUNT = "streak_count"
    const val KEY_LAST_ACTIVE_DATE = "last_active_date"
    const val KEY_TOTAL_FOCUS_SESSIONS = "total_focus_sessions"
    const val KEY_THEME_MODE = "theme_mode"

    // Database
    const val DATABASE_NAME = "selffocus_database"
    const val DATABASE_VERSION = 1

    // Time Formats
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val TIME_FORMAT_DISPLAY = "HH:mm"
    const val DATE_TIME_FORMAT_STORAGE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    // Limits
    const val MAX_LIMIT_MINUTES = 1440 // 24 hours
    const val MIN_LIMIT_MINUTES = 1

    // Misc
    const val ANIMATION_DURATION_MS = 300
    const val DEBOUNCE_DELAY_MS = 300L
}
