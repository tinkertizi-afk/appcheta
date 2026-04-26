package com.selffocus.domain.usecase

import com.selffocus.domain.model.AppUsageRecord
import com.selffocus.domain.repository.AppUsageRepository
import javax.inject.Inject

/**
 * Use case to fetch and store usage stats from system.
 */
class FetchUsageStats @Inject constructor(
    private val repository: AppUsageRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.fetchAndStoreUsageStats()
    }

    /**
     * Obtiene estadísticas de uso sin guardarlas en la base de datos.
     * Usado por el worker para verificaciones rápidas.
     */
    suspend fun invokeRaw(): List<AppUsageRecord> {
        return repository.getUsageStatsFromSystem()
    }
}
