package com.selffocus.data.repository

import android.content.pm.PackageManager
import android.app.usage.UsageStatsManager
import android.os.Build
import com.selffocus.data.local.AppUsageDao
import com.selffocus.data.local.AppUsageEntity
import com.selffocus.domain.model.AppCategory
import com.selffocus.domain.model.AppUsageRecord
import com.selffocus.domain.model.DailyUsageData
import com.selffocus.domain.model.UsageSummary
import com.selffocus.domain.repository.AppUsageRepository
import com.selffocus.core.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AppUsageRepository.
 */
@Singleton
class AppUsageRepositoryImpl @Inject constructor(
    private val dao: AppUsageDao,
    private val usageStatsManager: UsageStatsManager,
    private val packageManager: PackageManager
) : AppUsageRepository {

    override suspend fun fetchAndStoreUsageStats(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ) ?: emptyList()

            val today = TimeUtils.getCurrentDateString()
            val entities = usageStats.mapNotNull { stats ->
                val appName = getAppName(stats.packageName)
                if (appName == null || stats.totalTimeInForeground <= 0) {
                    null
                } else {
                    AppUsageEntity(
                        packageName = stats.packageName,
                        appName = appName,
                        totalTimeMs = stats.totalTimeInForeground,
                        lastTimeUsedMs = stats.lastTimeUsed,
                        launchCount = 0,
                        date = today,
                        category = categorizeApp(stats.packageName).name
                    )
                }
            }

            if (entities.isNotEmpty()) {
                dao.insertAll(entities)
            }
        }
    }

    override suspend fun getUsageStatsFromSystem(): List<AppUsageRecord> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()

        usageStats
            .filter { it.totalTimeInForeground > 0 }
            .mapNotNull { stats ->
                val appName = getAppName(stats.packageName)
                if (appName == null) null
                else AppUsageRecord(
                    packageName = stats.packageName,
                    appName = appName,
                    totalTimeInForeground = stats.totalTimeInForeground,
                    lastTimeUsed = stats.lastTimeUsed,
                    timestamp = System.currentTimeMillis()
                )
            }
    }

    override suspend fun getUsageForDate(date: String): Result<List<AppUsageRecord>> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getUsageForDate(date).map { it.toDomainModel() }
        }
    }

    override suspend fun getUsageForDateRange(startDate: String, endDate: String): Result<List<AppUsageRecord>> =
        withContext(Dispatchers.IO) {
            runCatching {
                dao.getUsageForDateRange(startDate, endDate).map { it.toDomainModel() }
            }
        }

    override fun getUsageSummaryFlow(date: String): Flow<Result<UsageSummary>> = flow {
        emit(
            runCatching {
                val records = dao.getUsageForDate(date).map { it.toDomainModel() }
                val totalTime = records.sumOf { it.totalTimeMs }
                val uniqueApps = records.size
                val topApps = records.sortedByDescending { it.totalTimeMs }.take(5)
                val categoryBreakdown = records.groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.totalTimeMs } }

                UsageSummary(
                    totalScreenTimeMs = totalTime,
                    uniqueAppsCount = uniqueApps,
                    topApps = topApps,
                    categoryBreakdown = categoryBreakdown,
                    date = date
                )
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun getDailyUsageData(days: Int): Result<List<DailyUsageData>> = withContext(Dispatchers.IO) {
        runCatching {
            val result = mutableListOf<DailyUsageData>()
            val calendar = Calendar.getInstance()

            for (i in 0 until days) {
                val date = TimeUtils.getDateStringFromCalendar(calendar)
                val totalTime = dao.getTotalTimeForDate(date) ?: 0L
                val appCount = dao.getAppCountForDate(date)

                result.add(
                    DailyUsageData(
                        date = date,
                        totalTimeMs = totalTime,
                        appCount = appCount
                    )
                )
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }

            result.reversed()
        }
    }

    override suspend fun getTotalTimeForDate(date: String): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getTotalTimeForDate(date) ?: 0L
        }
    }

    override suspend fun getAppCountForDate(date: String): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getAppCountForDate(date)
        }
    }

    override suspend fun deleteOldUsageData(daysToKeep: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysToKeep)
            val cutoffDate = TimeUtils.getDateStringFromCalendar(calendar)
            dao.deleteOlderThan(cutoffDate)
        }
    }

    override suspend fun getUsageStats(startTime: Long, endTime: Long): Result<List<AppUsageRecord>> = withContext(Dispatchers.IO) {
        runCatching {
            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ) ?: emptyList()

            usageStats
                .filter { it.totalTimeInForeground > 0 }
                .mapNotNull { stats ->
                    val appName = getAppName(stats.packageName)
                    if (appName == null) null
                    else AppUsageRecord(
                        packageName = stats.packageName,
                        appName = appName,
                        totalTimeInForeground = stats.totalTimeInForeground,
                        lastTimeUsed = stats.lastTimeUsed,
                        timestamp = System.currentTimeMillis()
                    )
                }
        }
    }

    override suspend fun getTodayUsage(): Result<List<AppUsageRecord>> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        getUsageStats(calendar.timeInMillis, System.currentTimeMillis())
    }

    override suspend fun getWeekUsage(): Result<List<AppUsageRecord>> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }
        getUsageStats(calendar.timeInMillis, System.currentTimeMillis())
    }

    override suspend fun getMonthUsage(): Result<List<AppUsageRecord>> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -30)
        }
        getUsageStats(calendar.timeInMillis, System.currentTimeMillis())
    }

    override suspend fun getUsageSummary(date: String): Result<UsageSummary?> = withContext(Dispatchers.IO) {
        runCatching {
            val records = dao.getUsageForDate(date).map { it.toDomainModel() }
            if (records.isEmpty()) return@runCatching null
            
            val totalTime = records.sumOf { it.totalTimeMs }
            val uniqueApps = records.size
            val topApps = records.sortedByDescending { it.totalTimeMs }.take(5)
            val categoryBreakdown = records.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.totalTimeMs } }

            UsageSummary(
                totalScreenTimeMs = totalTime,
                uniqueAppsCount = uniqueApps,
                topApps = topApps,
                categoryBreakdown = categoryBreakdown,
                date = date
            )
        }
    }

    override suspend fun saveUsageRecords(records: List<AppUsageRecord>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val entities = records.map { record ->
                AppUsageEntity(
                    packageName = record.packageName,
                    appName = record.appName,
                    totalTimeMs = record.totalTimeInForeground,
                    lastTimeUsedMs = record.lastTimeUsed,
                    launchCount = 0,
                    date = TimeUtils.getCurrentDateString(),
                    category = categorizeApp(record.packageName).name
                )
            }
            dao.insertAll(entities)
        }
    }

    override suspend fun clearOldRecords(olderThanDays: Int): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -olderThanDays)
            }
            val cutoffDate = TimeUtils.getDateStringFromCalendar(calendar)
            dao.deleteOlderThan(cutoffDate)
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        ).getOrNull() ?: Result.success(0)
    }

    override fun hasUsageStatsPermission(): Boolean {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.MINUTE, -5)
        val startTime = calendar.timeInMillis
        
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )?.isNotEmpty() == true
    }

    private fun getAppName(packageName: String): String? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun categorizeApp(packageName: String): AppCategory {
        return when {
            packageName.contains("facebook") || packageName.contains("instagram") ||
                    packageName.contains("twitter") || packageName.contains("tiktok") -> AppCategory.SOCIAL
            packageName.contains("youtube") || packageName.contains("netflix") ||
                    packageName.contains("spotify") -> AppCategory.ENTERTAINMENT
            packageName.contains("gmail") || packageName.contains("whatsapp") ||
                    packageName.contains("telegram") || packageName.contains("messenger") -> AppCategory.COMMUNICATION
            packageName.contains("docs") || packageName.contains("sheets") ||
                    packageName.contains("drive") || packageName.contains("notion") -> AppCategory.PRODUCTIVITY
            packageName.contains("chrome") || packageName.contains("firefox") ||
                    packageName.contains("edge") -> AppCategory.UTILITIES
            else -> AppCategory.OTHER
        }
    }
}
