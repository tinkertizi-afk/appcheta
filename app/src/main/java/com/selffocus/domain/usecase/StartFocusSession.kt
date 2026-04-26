package com.selffocus.domain.usecase

import com.selffocus.domain.repository.FocusSessionRepository
import javax.inject.Inject

/**
 * Use case to start a focus session.
 */
class StartFocusSession @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(
        durationMinutes: Int,
        targetApps: List<String>? = null,
        notes: String? = null
    ): Result<Long> {
        return repository.startSession(durationMinutes, targetApps, notes)
    }
}
