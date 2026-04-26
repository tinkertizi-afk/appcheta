package com.selffocus.domain.repository

import com.selffocus.domain.model.AppUsageRecord
import com.selffocus.domain.model.DailyUsageData
import com.selffocus.domain.model.UsageSummary

/**
 * Repository interface for app usage statistics operations.
 */
interface AppUsageRepository {

    /**
     * Fetches usage stats for all apps within a time range.
     * @param startTime Start timestamp in milliseconds
     * @param endTime End timestamp in milliseconds
     * @return List of AppUsageRecord or empty list on error
     */
    suspend fun getUsageStats(startTime: Long, endTime: Long): Result<List<AppUsageRecord>>

    /**
     * Gets usage stats for today.
     * @return List of AppUsageRecord for current day
     */
    suspend fun getTodayUsage(): Result<List<AppUsageRecord>>

    /**
     * Gets usage stats for the last 7 days.
     * @return List of AppUsageRecord for last week
     */
    suspend fun getWeekUsage(): Result<List<AppUsageRecord>>

    /**
     * Gets usage stats for the last 30 days.
     * @return List of AppUsageRecord for last month
     */
    suspend fun getMonthUsage(): Result<List<AppUsageRecord>>

    /**
     * Gets aggregated usage summary for a specific date.
     * @param date Date string in yyyy-MM-dd format
     * @return UsageSummary or null if no data
     */
    suspend fun getUsageSummary(date: String): Result<UsageSummary?>

    /**
     * Gets daily usage data for chart display.
     * @param days Number of days to fetch
     * @return List of DailyUsageData
     */
    suspend fun getDailyUsageData(days: Int = 7): Result<List<DailyUsageData>>

    /**
     * Gets usage time for a specific package today.
     * @param packageName Package name to query
     * @return Total time in milliseconds
     */
    suspend fun getUsageTimeForPackage(packageName: String): Result<Long>

    /**
     * Saves usage records to local database.
     * @param records List of AppUsageRecord to save
     * @return Unit or error
     */
    suspend fun saveUsageRecords(records: List<AppUsageRecord>): Result<Unit>

    /**
     * Clears old usage records beyond retention period.
     * @param olderThanDays Delete records older than this many days
     * @return Number of deleted records
     */
    suspend fun clearOldRecords(olderThanDays: Int = 30): Result<Int>

    /**
     * Checks if usage stats permission is granted.
     * @return true if permission granted
     */
    fun hasUsageStatsPermission(): Boolean

    /**
     * Fetches and stores usage stats from system.
     * @return Unit or error
     */
    suspend fun fetchAndStoreUsageStats(): Result<Unit>

    /**
     * Gets usage stats directly from system without storing.
     * @return List of AppUsageRecord
     */
    suspend fun getUsageStatsFromSystem(): List<AppUsageRecord>
}
