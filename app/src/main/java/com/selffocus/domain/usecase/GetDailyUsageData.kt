package com.selffocus.domain.usecase

import com.selffocus.domain.repository.AppUsageRepository
import javax.inject.Inject

/**
 * Use case to get daily usage data for charts.
 */
class GetDailyUsageData @Inject constructor(
    private val repository: AppUsageRepository
) {
    suspend operator fun invoke(days: Int = 7): Result<List<com.selffocus.domain.model.DailyUsageData>> {
        return repository.getDailyUsageData(days)
    }
}
