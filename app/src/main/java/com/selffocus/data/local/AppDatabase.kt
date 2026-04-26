package com.selffocus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.selffocus.core.constants.AppConstants
import com.selffocus.domain.model.AppCategory
import com.selffocus.domain.model.DailyLimit
import com.selffocus.domain.model.FocusSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Entity for app usage records.
 */
@androidx.room.Entity(tableName = "app_usage")
data class AppUsageEntity(
    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val totalTimeMs: Long,
    val lastTimeUsedMs: Long,
    val launchCount: Int,
    val date: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): com.selffocus.domain.model.AppUsageRecord {
        return com.selffocus.domain.model.AppUsageRecord(
            packageName = packageName,
            appName = appName,
            totalTimeMs = totalTimeMs,
            lastTimeUsedMs = lastTimeUsedMs,
            launchCount = launchCount,
            date = date,
            category = AppCategory.valueOf(category)
        )
    }

    companion object {
        fun fromDomainModel(record: com.selffocus.domain.model.AppUsageRecord): AppUsageEntity {
            return AppUsageEntity(
                packageName = record.packageName,
                appName = record.appName,
                totalTimeMs = record.totalTimeMs,
                lastTimeUsedMs = record.lastTimeUsedMs,
                launchCount = record.launchCount,
                date = record.date,
                category = record.category.name
            )
        }
    }
}

/**
 * Entity for daily app limits.
 */
@androidx.room.Entity(tableName = "daily_limits")
data class LimitEntity(
    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val limitMinutes: Int,
    val isEnabled: Boolean = true,
    val category: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): DailyLimit {
        return DailyLimit(
            id = id,
            packageName = packageName,
            appName = appName,
            limitMinutes = limitMinutes,
            isEnabled = isEnabled,
            category = category?.let { AppCategory.valueOf(it) }
        )
    }

    companion object {
        fun fromDomainModel(limit: DailyLimit): LimitEntity {
            return LimitEntity(
                id = limit.id,
                packageName = limit.packageName,
                appName = limit.appName,
                limitMinutes = limit.limitMinutes,
                isEnabled = limit.isEnabled,
                category = limit.category?.name
            )
        }
    }
}

/**
 * Entity for focus sessions.
 */
@androidx.room.Entity(tableName = "focus_sessions")
data class SessionEntity(
    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMs: Long,
    val endTimeMs: Long?,
    val durationMinutes: Int,
    val actualDurationMs: Long = 0,
    val isCompleted: Boolean = false,
    val targetApps: String? = null, // JSON serialized list of package names
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): FocusSession {
        return FocusSession(
            id = id,
            startTimeMs = startTimeMs,
            endTimeMs = endTimeMs,
            durationMinutes = durationMinutes,
            actualDurationMs = actualDurationMs,
            isCompleted = isCompleted,
            targetApps = targetApps,
            notes = notes
        )
    }

    companion object {
        fun fromDomainModel(session: FocusSession): SessionEntity {
            return SessionEntity(
                id = session.id,
                startTimeMs = session.startTimeMs,
                endTimeMs = session.endTimeMs,
                durationMinutes = session.durationMinutes,
                actualDurationMs = session.actualDurationMs,
                isCompleted = session.isCompleted,
                targetApps = session.targetApps,
                notes = session.notes
            )
        }
    }
}

/**
 * DAO for app usage operations.
 */
@androidx.room.Dao
interface AppUsageDao {

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(usage: AppUsageEntity)

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAll(usages: List<AppUsageEntity>)

    @androidx.room.Query("SELECT * FROM app_usage WHERE date = :date ORDER BY totalTimeMs DESC")
    suspend fun getUsageForDate(date: String): List<AppUsageEntity>

    @androidx.room.Query("SELECT * FROM app_usage WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, totalTimeMs DESC")
    suspend fun getUsageForDateRange(startDate: String, endDate: String): List<AppUsageEntity>

    @androidx.room.Query("SELECT DISTINCT date FROM app_usage ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentDates(limit: Int = 30): List<String>

    @androidx.room.Query("DELETE FROM app_usage WHERE date < :date")
    suspend fun deleteOlderThan(date: String)

    @androidx.room.Query("SELECT SUM(totalTimeMs) FROM app_usage WHERE date = :date")
    suspend fun getTotalTimeForDate(date: String): Long?

    @androidx.room.Query("SELECT COUNT(DISTINCT packageName) FROM app_usage WHERE date = :date")
    suspend fun getAppCountForDate(date: String): Int
}

/**
 * DAO for daily limit operations.
 */
@androidx.room.Dao
interface LimitDao {

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(limit: LimitEntity): Long

    @androidx.room.Update
    suspend fun update(limit: LimitEntity)

    @androidx.room.Delete
    suspend fun delete(limit: LimitEntity)

    @androidx.room.Query("SELECT * FROM daily_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getLimitForPackage(packageName: String): LimitEntity?

    @androidx.room.Query("SELECT * FROM daily_limits WHERE isEnabled = 1 ORDER BY appName ASC")
    suspend fun getAllEnabledLimits(): List<LimitEntity>

    @androidx.room.Query("SELECT * FROM daily_limits ORDER BY appName ASC")
    suspend fun getAllLimits(): List<LimitEntity>

    @androidx.room.Query("UPDATE daily_limits SET isEnabled = :enabled, updatedAt = :timestamp WHERE id = :id")
    suspend fun toggleLimit(id: Long, enabled: Boolean, timestamp: Long = System.currentTimeMillis())

    @androidx.room.Query("DELETE FROM daily_limits WHERE id = :id")
    suspend fun deleteById(id: Long)
}

/**
 * DAO for focus session operations.
 */
@androidx.room.Dao
interface SessionDao {

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    @androidx.room.Update
    suspend fun update(session: SessionEntity)

    @androidx.room.Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): SessionEntity?

    @androidx.room.Query("SELECT * FROM focus_sessions WHERE isCompleted = 0 ORDER BY startTimeMs DESC LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    @androidx.room.Query("SELECT * FROM focus_sessions ORDER BY startTimeMs DESC LIMIT :limit")
    suspend fun getRecentSessions(limit: Int = 20): List<SessionEntity>

    @androidx.room.Query("SELECT * FROM focus_sessions WHERE startTimeMs BETWEEN :startMs AND :endMs ORDER BY startTimeMs DESC")
    suspend fun getSessionsForDateRange(startMs: Long, endMs: Long): List<SessionEntity>

    @androidx.room.Query("SELECT COUNT(*) FROM focus_sessions WHERE isCompleted = 1")
    suspend fun getTotalCompletedSessions(): Int

    @androidx.room.Query("SELECT SUM(actualDurationMs) FROM focus_sessions WHERE isCompleted = 1")
    suspend fun getTotalFocusTimeMs(): Long?

    @androidx.room.Query("UPDATE focus_sessions SET endTimeMs = :endMs, actualDurationMs = :durationMs, isCompleted = 1 WHERE id = :id")
    suspend fun completeSession(id: Long, endMs: Long, durationMs: Long)
}

/**
 * Room database for SelfFocus.
 */
@Database(
    entities = [AppUsageEntity::class, LimitEntity::class, SessionEntity::class],
    version = AppConstants.DATABASE_VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appUsageDao(): AppUsageDao
    abstract fun limitDao(): LimitDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    AppConstants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
