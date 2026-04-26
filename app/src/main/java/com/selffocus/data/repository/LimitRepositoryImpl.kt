package com.selffocus.data.repository

import com.selffocus.data.local.LimitDao
import com.selffocus.data.local.LimitEntity
import com.selffocus.data.prefs.DataStoreManager
import com.selffocus.domain.model.AppCategory
import com.selffocus.domain.model.DailyLimit
import com.selffocus.domain.repository.LimitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of LimitRepository.
 */
@Singleton
class LimitRepositoryImpl @Inject constructor(
    private val dao: LimitDao,
    private val dataStoreManager: DataStoreManager
) : LimitRepository {

    override suspend fun setAppLimit(packageName: String, appName: String, minutes: Int): Result<Long> =
        withContext(Dispatchers.IO) {
            runCatching {
                val existing = dao.getLimitForPackage(packageName)
                if (existing != null) {
                    val updated = existing.copy(
                        limitMinutes = minutes,
                        updatedAt = System.currentTimeMillis()
                    )
                    dao.update(updated)
                    updated.id
                } else {
                    val newLimit = LimitEntity(
                        packageName = packageName,
                        appName = appName,
                        limitMinutes = minutes,
                        isEnabled = true
                    )
                    dao.insert(newLimit)
                }
            }
        }

    override suspend fun setCategoryLimit(category: AppCategory, minutes: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            // Store in DataStore as category limits are simpler
            dataStoreManager.setDefaultDailyLimit(minutes)
        }
    }

    override suspend fun getLimitForPackage(packageName: String): Result<DailyLimit?> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getLimitForPackage(packageName)?.toDomainModel()
        }
    }

    override suspend fun getAllLimits(): Result<List<DailyLimit>> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getAllLimits().map { it.toDomainModel() }
        }
    }

    override suspend fun getEnabledLimits(): Result<List<DailyLimit>> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getAllEnabledLimits().map { it.toDomainModel() }
        }
    }

    override suspend fun toggleLimit(id: Long, enabled: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dao.toggleLimit(id, enabled)
        }
    }

    override suspend fun deleteLimit(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dao.deleteById(id)
        }
    }

    override suspend fun deleteLimit(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dao.getLimitForPackage(packageName)?.let {
                dao.deleteById(it.id)
            }
        }
    }

    override fun getDefaultLimitFlow(): Flow<Int> {
        return dataStoreManager.defaultDailyLimitMinutes
    }

    override suspend fun updateDefaultLimit(minutes: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStoreManager.setDefaultDailyLimit(minutes)
        }
    }
}
