package com.selffocus.data.repository

import com.selffocus.data.local.SessionDao
import com.selffocus.data.local.SessionEntity
import com.selffocus.data.prefs.DataStoreManager
import com.selffocus.domain.model.FocusSession
import com.selffocus.domain.repository.FocusSessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FocusSessionRepository.
 */
@Singleton
class FocusSessionRepositoryImpl @Inject constructor(
    private val dao: SessionDao,
    private val dataStoreManager: DataStoreManager
) : FocusSessionRepository {

    override suspend fun startSession(
        durationMinutes: Int,
        targetApps: List<String>?,
        notes: String?
    ): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
            val session = SessionEntity(
                startTimeMs = System.currentTimeMillis(),
                endTimeMs = null,
                durationMinutes = durationMinutes,
                targetApps = targetApps?.joinToString(","),
                notes = notes
            )
            dao.insert(session)
        }
    }

    override suspend fun completeSession(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val session = dao.getSessionById(id) ?: throw IllegalStateException("Session not found")
            val endTime = System.currentTimeMillis()
            val actualDuration = endTime - session.startTimeMs
            dao.completeSession(id, endTime, actualDuration)
            
            // Update total sessions count
            dataStoreManager.incrementTotalFocusSessions()
        }
    }

    override suspend fun cancelSession(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            // Simply mark as not completed with minimal duration
            val session = dao.getSessionById(id)
            if (session != null) {
                val updated = session.copy(
                    endTimeMs = System.currentTimeMillis(),
                    actualDurationMs = System.currentTimeMillis() - session.startTimeMs,
                    isCompleted = false
                )
                dao.update(updated)
            }
        }
    }

    override suspend fun getSessionById(id: Long): Result<FocusSession?> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getSessionById(id)?.toDomainModel()
        }
    }

    override suspend fun getActiveSession(): Result<FocusSession?> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getActiveSession()?.toDomainModel()
        }
    }

    override suspend fun getRecentSessions(limit: Int): Result<List<FocusSession>> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getRecentSessions(limit).map { it.toDomainModel() }
        }
    }

    override suspend fun getTotalCompletedSessions(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getTotalCompletedSessions()
        }
    }

    override suspend fun getTotalFocusTimeMs(): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getTotalFocusTimeMs() ?: 0L
        }
    }

    override fun getTotalSessionsFlow(): Flow<Int> {
        return dataStoreManager.totalFocusSessions
    }

    override fun getStreakCountFlow(): Flow<Int> {
        return dataStoreManager.streakCount
    }

    override suspend fun incrementStreak(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStoreManager.incrementStreak()
        }
    }

    override suspend fun resetStreak(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStoreManager.resetStreak()
        }
    }
}
