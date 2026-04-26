package com.selffocus.domain.repository

import com.selffocus.domain.model.DailyLimit
import com.selffocus.domain.model.LimitWithUsage

/**
 * Repository interface for app limit operations.
 */
interface LimitRepository {

    /**
     * Gets all active limits.
     * @return List of DailyLimit
     */
    suspend fun getAllLimits(): Result<List<DailyLimit>>

    /**
     * Gets limits with current usage data.
     * @return List of LimitWithUsage
     */
    suspend fun getLimitsWithUsage(): Result<List<LimitWithUsage>>

    /**
     * Gets a specific limit by package name.
     * @param packageName Package name to query
     * @return DailyLimit or null if not found
     */
    suspend fun getLimitByPackage(packageName: String): Result<DailyLimit?>

    /**
     * Gets the global/default limit.
     * @return DailyLimit or null if not set
     */
    suspend fun getGlobalLimit(): Result<DailyLimit?>

    /**
     * Creates or updates a limit.
     * @param limit DailyLimit to save
     * @return Saved DailyLimit with updated ID
     */
    suspend fun saveLimit(limit: DailyLimit): Result<DailyLimit>

    /**
     * Deletes a limit by ID.
     * @param id Limit ID to delete
     * @return Unit or error
     */
    suspend fun deleteLimit(id: Long): Result<Unit>

    /**
     * Enables or disables a limit.
     * @param id Limit ID to toggle
     * @param isEnabled New enabled state
     * @return Updated DailyLimit
     */
    suspend fun toggleLimit(id: Long, isEnabled: Boolean): Result<DailyLimit>

    /**
     * Checks if usage exceeds limit for a package.
     * @param packageName Package name to check
     * @return true if limit exceeded
     */
    suspend fun isLimitExceeded(packageName: String): Result<Boolean>

    /**
     * Gets remaining time before limit is reached.
     * @param packageName Package name to check
     * @return Remaining time in milliseconds, or -1 if no limit set
     */
    suspend fun getRemainingTime(packageName: String): Result<Long>

    /**
     * Sets default global limit for all apps.
     * @param minutes Default limit in minutes
     * @return Created/updated DailyLimit
     */
    suspend fun setGlobalDefaultLimit(minutes: Int): Result<DailyLimit>
}
