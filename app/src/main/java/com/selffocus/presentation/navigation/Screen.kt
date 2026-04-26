package com.selffocus.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes for the app.
 */
sealed class Screen {
    @Serializable
    object Dashboard : Screen()

    @Serializable
    object Limits : Screen()

    @Serializable
    object Focus : Screen()

    @Serializable
    object Settings : Screen()
}
