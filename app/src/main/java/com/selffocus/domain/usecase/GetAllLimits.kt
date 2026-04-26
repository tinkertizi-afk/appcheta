package com.selffocus.domain.usecase

import com.selffocus.domain.repository.LimitRepository
import javax.inject.Inject

/**
 * Use case to get all app limits.
 */
class GetAllLimits @Inject constructor(
    private val repository: LimitRepository
) {
    suspend operator fun invoke(): Result<List<com.selffocus.domain.model.DailyLimit>> {
        return repository.getAllLimits()
    }
}
