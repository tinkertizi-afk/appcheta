package com.selffocus.domain.usecase

import com.selffocus.domain.repository.LimitRepository
import javax.inject.Inject

/**
 * Use case to set app usage limit.
 */
class SetAppLimit @Inject constructor(
    private val repository: LimitRepository
) {
    suspend operator fun invoke(packageName: String, appName: String, minutes: Int): Result<Long> {
        return repository.setAppLimit(packageName, appName, minutes)
    }
}
