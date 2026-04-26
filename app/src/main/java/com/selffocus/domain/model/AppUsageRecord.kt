package com.selffocus.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing app usage statistics for a specific package.
 */
@Serializable
data class AppUsageRecord(
    val packageName: String,
    val appName: String,
    val totalTimeMs: Long,
    val lastTimeUsedMs: Long,
    val launchCount: Int = 0,
    val date: String, // ISO date string (yyyy-MM-dd)
    val category: AppCategory = AppCategory.OTHER
) {
    /**
     * Returns total time in minutes.
     */
    fun getTotalMinutes(): Long = totalTimeMs / (1000 * 60)

    /**
     * Returns formatted time string (e.g., "2h 30m").
     */
    fun getFormattedTime(): String {
        val hours = totalTimeMs / (1000 * 60 * 60)
        val minutes = (totalTimeMs % (1000 * 60 * 60)) / (1000 * 60)
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}

/**
 * App categories for grouping and filtering.
 */
enum class AppCategory {
    SOCIAL,
    ENTERTAINMENT,
    PRODUCTIVITY,
    COMMUNICATION,
    GAMES,
    UTILITIES,
    EDUCATION,
    SHOPPING,
    NEWS,
    OTHER
}

/**
 * Aggregated usage data for display in dashboard.
 */
data class UsageSummary(
    val totalScreenTimeMs: Long,
    val uniqueAppsCount: Int,
    val topApps: List<AppUsageRecord>,
    val categoryBreakdown: Map<AppCategory, Long>,
    val date: String
) {
    fun getFormattedTotalTime(): String {
        val hours = totalScreenTimeMs / (1000 * 60 * 60)
        val minutes = (totalScreenTimeMs % (1000 * 60 * 60)) / (1000 * 60)
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}

/**
 * Daily usage data for chart display.
 */
data class DailyUsageData(
    val date: String,
    val totalTimeMs: Long,
    val appCount: Int
)
