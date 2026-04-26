package com.selffocus.data.repository

import com.selffocus.data.prefs.DataStoreManager
import com.selffocus.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PreferencesRepository.
 */
@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : PreferencesRepository {

    override val focusModeEnabled: Flow<Boolean> = dataStoreManager.focusModeEnabled

    override val streakCount: Flow<Int> = dataStoreManager.streakCount

    override val totalFocusSessions: Flow<Int> = dataStoreManager.totalFocusSessions

    override val themeMode: Flow<String> = dataStoreManager.themeMode

    override suspend fun setFocusModeEnabled(enabled: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStoreManager.setFocusModeEnabled(enabled)
        }
    }

    override suspend fun setThemeMode(mode: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStoreManager.setThemeMode(mode)
        }
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

    override suspend fun setLastActiveDate(date: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStoreManager.setLastActiveDate(date)
        }
    }

    override suspend fun getLastActiveDate(): Flow<String?> = withContext(Dispatchers.IO) {
        dataStoreManager.lastActiveDate
    }

    override suspend fun clearAllPreferences(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStoreManager.clearAll()
        }
    }
}
