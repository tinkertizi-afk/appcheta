package com.selffocus.core.util

/**
 * Maps exceptions to user-friendly error messages.
 */
object ErrorMapper {

    fun mapToErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is SecurityException -> "Permiso denegado. Concede el permiso en Ajustes."
            is IllegalArgumentException -> "Datos inválidos. Verifica la información ingresada."
            is java.io.IOException -> "Error de almacenamiento. Verifica el espacio disponible."
            is java.util.concurrent.CancellationException -> "Operación cancelada."
            is kotlinx.coroutines.TimeoutCancellationException -> "Tiempo de espera agotado."
            else -> throwable.message ?: "Ocurrió un error inesperado."
        }
    }

    fun mapToErrorResource(throwable: Throwable): String {
        return when (throwable) {
            is SecurityException -> "permission_denied"
            is IllegalArgumentException -> "invalid_data"
            is java.io.IOException -> "storage_error"
            else -> "unknown_error"
        }
    }
}
