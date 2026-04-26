package com.selffocus.core.util

/**
 * Represents the result of an operation with data or error.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

/**
 * Wrapper for result operations using Kotlin's Result type.
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Failure(val error: Throwable) : ResultWrapper<Nothing>()

    companion object {
        inline fun <T> runCatching(block: () -> T): ResultWrapper<T> {
            return try {
                Success(block())
            } catch (e: Exception) {
                Failure(e)
            }
        }
    }
}

/**
 * Maps a Result to a ResultWrapper.
 */
fun <T> Result<T>.toResultWrapper(): ResultWrapper<T> {
    return fold(
        onSuccess = { ResultWrapper.Success(it) },
        onFailure = { ResultWrapper.Failure(it) }
    )
}
