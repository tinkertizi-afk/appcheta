package com.selffocus.core.constants

import kotlinx.serialization.Serializable

/**
 * Navigation routes for the app.
 */
@Serializable
sealed class Routes {
    @Serializable
    data object Dashboard : Routes()

    @Serializable
    data object AppLimits : Routes()

    @Serializable
    data object FocusTimer : Routes()

    @Serializable
    data object Settings : Routes()

    @Serializable
    data class AppLimitDetail(val packageName: String) : Routes()
}

/**
 * Screen titles corresponding to routes.
 */
object ScreenTitles {
    const val DASHBOARD = "Dashboard"
    const val APP_LIMITS = "Límites de Apps"
    const val FOCUS_TIMER = "Modo Enfoque"
    const val SETTINGS = "Ajustes"
}
