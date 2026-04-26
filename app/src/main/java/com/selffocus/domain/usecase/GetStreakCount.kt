package com.selffocus.domain.usecase

import com.selffocus.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get streak count.
 */
class GetStreakCount @Inject constructor(
    private val repository: FocusSessionRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getStreakCountFlow()
    }
}
