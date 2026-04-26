package com.selffocus.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a focus session.
 */
@Serializable
data class FocusSession(
    val id: Long = 0,
    val startTimeMs: Long,
    val endTimeMs: Long? = null,
    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val wasInterrupted: Boolean = false,
    val targetApps: List<String> = emptyList(), // Empty means all apps
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Returns planned duration in milliseconds.
     */
    fun getPlannedDurationMs(): Long = plannedDurationMinutes.toLong() * 60 * 1000

    /**
     * Returns actual duration in milliseconds.
     */
    fun getActualDurationMs(): Long = actualDurationMinutes.toLong() * 60 * 1000

    /**
     * Calculates completion percentage.
     */
    fun getCompletionPercentage(): Float {
        return if (actualDurationMinutes > 0 && plannedDurationMinutes > 0) {
            ((actualDurationMinutes.toFloat() / plannedDurationMinutes.toFloat()) * 100)
                .coerceIn(0f, 100f)
        } else {
            0f
        }
    }

    /**
     * Checks if session is currently active.
     */
    fun isActive(): Boolean = endTimeMs == null

    /**
     * Gets formatted session duration.
     */
    fun getFormattedDuration(): String {
        val hours = plannedDurationMinutes / 60
        val minutes = plannedDurationMinutes % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}

/**
 * Focus session status for UI display.
 */
enum class FocusSessionStatus {
    NOT_STARTED,
    ACTIVE,
    PAUSED,
    COMPLETED,
    INTERRUPTED
}

/**
 * Active session state for real-time UI updates.
 */
data class ActiveFocusSession(
    val session: FocusSession,
    val remainingTimeMs: Long,
    val elapsedSeconds: Int,
    val status: FocusSessionStatus
) {
    fun getFormattedRemainingTime(): String {
        val hours = remainingTimeMs / (1000 * 60 * 60)
        val minutes = (remainingTimeMs % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (remainingTimeMs % (1000 * 60)) / 1000
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun getProgressPercentage(): Float {
        val totalMs = session.getPlannedDurationMs()
        return if (totalMs > 0) {
            ((totalMs - remainingTimeMs).toFloat() / totalMs.toFloat() * 100).coerceIn(0f, 100f)
        } else {
            0f
        }
    }
}

/**
 * Focus session statistics.
 */
data class FocusStats(
    val totalSessions: Int,
    val completedSessions: Int,
    val totalFocusTimeMinutes: Long,
    val averageSessionMinutes: Long,
    val currentStreak: Int,
    val longestStreak: Int
) {
    fun getCompletionRate(): Float {
        return if (totalSessions > 0) {
            (completedSessions.toFloat() / totalSessions.toFloat()) * 100
        } else {
            0f
        }
    }
}
