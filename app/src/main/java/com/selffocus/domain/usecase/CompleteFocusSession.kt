package com.selffocus.domain.usecase

import com.selffocus.domain.repository.FocusSessionRepository
import javax.inject.Inject

/**
 * Use case to complete a focus session.
 */
class CompleteFocusSession @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(sessionId: Long): Result<Unit> {
        return repository.completeSession(sessionId)
    }
}
