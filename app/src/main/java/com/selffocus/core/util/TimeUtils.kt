package com.selffocus.core.util

import android.content.Context
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for Time operations.
 */
object TimeUtils {

    private const val MS_PER_SECOND = 1000L
    private const val MS_PER_MINUTE = 60 * MS_PER_SECOND
    private const val MS_PER_HOUR = 60 * MS_PER_MINUTE

    /**
     * Formats milliseconds to human-readable time string (e.g., "2h 30m").
     */
    fun formatDuration(ms: Long): String {
        val hours = ms / MS_PER_HOUR
        val minutes = (ms % MS_PER_HOUR) / MS_PER_MINUTE
        val seconds = (ms % MS_PER_MINUTE) / MS_PER_SECOND

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0) append("${minutes}m ")
            if (seconds > 0 || isEmpty()) append("${seconds}s")
        }.trim()
    }

    /**
     * Formats milliseconds to minutes.
     */
    fun toMinutes(ms: Long): Long = ms / MS_PER_MINUTE

    /**
     * Converts minutes to milliseconds.
     */
    fun fromMinutes(minutes: Long): Long = minutes * MS_PER_MINUTE

    /**
     * Gets start of day timestamp in milliseconds.
     */
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    /**
     * Gets end of day timestamp in milliseconds.
     */
    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    /**
     * Gets start of week (Monday) timestamp in milliseconds.
     */
    fun getStartOfWeek(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Set to Monday as start of week
            while (get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                add(Calendar.DAY_OF_MONTH, -1)
            }
        }
        return calendar.timeInMillis
    }

    /**
     * Gets start of month timestamp in milliseconds.
     */
    fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    /**
     * Checks if two timestamps are on the same day.
     */
    fun isSameDay(time1: Long, time2: Long): Boolean {
        return getStartOfDay(time1) == getStartOfDay(time2)
    }

    /**
     * Formats date for display.
     */
    fun formatDate(timestamp: Long, pattern: String = "MMM dd, yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Gets current date string for storage.
     */
    fun getCurrentDateKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}

/**
 * Extension function to check if usage stats permission is granted.
 */
fun Context.hasUsageStatsPermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as? android.app.AppOpsManager
        ?: return false
    val mode = appOps.checkOpNoThrow(
        android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        packageName
    )
    return mode == android.app.AppOpsManager.MODE_ALLOWED
}

/**
 * Extension function to get app label from package name.
 */
fun Context.getAppName(packageName: String): String {
    return try {
        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.FLAG_META_DATA)
        packageManager.getApplicationLabel(appInfo).toString()
    } catch (e: Exception) {
        packageName
    }
}

/**
 * Extension function to get app icon from package name.
 */
fun Context.getAppIcon(packageName: String): android.graphics.drawable.Drawable? {
    return try {
        packageManager.getApplicationIcon(packageName)
    } catch (e: Exception) {
        null
    }
}
