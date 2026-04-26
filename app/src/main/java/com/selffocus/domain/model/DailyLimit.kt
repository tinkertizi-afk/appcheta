package com.selffocus.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a daily limit for an app or category.
 */
@Serializable
data class DailyLimit(
    val id: Long = 0,
    val packageName: String, // Use "*" for global default limit
    val appName: String = "",
    val limitMinutes: Int,
    val isEnabled: Boolean = true,
    val notificationEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Returns limit in milliseconds.
     */
    fun getLimitMs(): Long = limitMinutes.toLong() * 60 * 1000

    /**
     * Checks if this is a global/default limit.
     */
    fun isGlobalLimit(): Boolean = packageName == "*"

    /**
     * Calculates percentage of limit used.
     */
    fun calculateUsagePercentage(usedTimeMs: Long): Float {
        return ((usedTimeMs.toFloat() / getLimitMs().toFloat()) * 100).coerceIn(0f, 999f)
    }
}

/**
 * Limit status indicating current state relative to usage.
 */
enum class LimitStatus {
    UNDER_LIMIT,      // < 80%
    APPROACHING,      // 80-99%
    AT_LIMIT,         // 100%
    EXCEEDED          // > 100%
}

/**
 * Limit with current usage data.
 */
data class LimitWithUsage(
    val limit: DailyLimit,
    val usedTimeMs: Long,
    val remainingTimeMs: Long,
    val status: LimitStatus
) {
    fun getUsagePercentage(): Float {
        return limit.calculateUsagePercentage(usedTimeMs)
    }

    fun getFormattedUsedTime(): String {
        val hours = usedTimeMs / (1000 * 60 * 60)
        val minutes = (usedTimeMs % (1000 * 60 * 60)) / (1000 * 60)
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    fun getFormattedRemainingTime(): String {
        if (remainingTimeMs <= 0) return "0m"
        val hours = remainingTimeMs / (1000 * 60 * 60)
        val minutes = (remainingTimeMs % (1000 * 60 * 60)) / (1000 * 60)
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}
