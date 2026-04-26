package com.selffocus.domain.repository

import com.selffocus.domain.model.ActiveFocusSession
import com.selffocus.domain.model.FocusSession
import com.selffocus.domain.model.FocusStats

/**
 * Repository interface for focus session operations.
 */
interface FocusSessionRepository {

    /**
     * Creates a new focus session.
     * @param durationMinutes Planned duration in minutes
     * @param targetApps Optional list of apps to focus on (empty = all)
     * @return Created FocusSession
     */
    suspend fun createSession(durationMinutes: Int, targetApps: List<String> = emptyList()): Result<FocusSession>

    /**
     * Completes an active session.
     * @param sessionId Session ID to complete
     * @return Updated FocusSession
     */
    suspend fun completeSession(sessionId: Long): Result<FocusSession>

    /**
     * Interrupts/cancels an active session.
     * @param sessionId Session ID to interrupt
     * @return Updated FocusSession
     */
    suspend fun interruptSession(sessionId: Long): Result<FocusSession>

    /**
     * Gets the currently active session if any.
     * @return ActiveFocusSession or null
     */
    suspend fun getActiveSession(): Result<ActiveFocusSession?>

    /**
     * Gets a session by ID.
     * @param sessionId Session ID to fetch
     * @return FocusSession or null
     */
    suspend fun getSessionById(sessionId: Long): Result<FocusSession?>

    /**
     * Gets all sessions for a specific date.
     * @param date Date string in yyyy-MM-dd format
     * @return List of FocusSession
     */
    suspend fun getSessionsByDate(date: String): Result<List<FocusSession>>

    /**
     * Gets sessions for the last N days.
     * @param days Number of days to fetch
     * @return List of FocusSession
     */
    suspend fun getRecentSessions(days: Int = 7): Result<List<FocusSession>>

    /**
     * Gets focus session statistics.
     * @return FocusStats
     */
    suspend fun getFocusStats(): Result<FocusStats>

    /**
     * Updates session notes.
     * @param sessionId Session ID to update
     * @param notes New notes text
     * @return Updated FocusSession
     */
    suspend fun updateSessionNotes(sessionId: Long, notes: String): Result<FocusSession>

    /**
     * Deletes a session by ID.
     * @param sessionId Session ID to delete
     * @return Unit or error
     */
    suspend fun deleteSession(sessionId: Long): Result<Unit>

    /**
     * Clears all completed sessions older than specified days.
     * @param olderThanDays Delete sessions older than this many days
     * @return Number of deleted sessions
     */
    suspend fun clearOldSessions(olderThanDays: Int = 30): Result<Int>
}
