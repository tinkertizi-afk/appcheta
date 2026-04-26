package com.selffocus.domain.usecase

import com.selffocus.domain.repository.AppUsageRepository
import com.selffocus.domain.model.UsageSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get usage summary for a specific date.
 */
class GetUsageSummary @Inject constructor(
    private val repository: AppUsageRepository
) {
    operator fun invoke(date: String): Flow<Result<UsageSummary>> {
        return repository.getUsageSummaryFlow(date)
    }
}
