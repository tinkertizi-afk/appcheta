package com.selffocus

import android.app.Application
import androidx.work.*
import com.selffocus.data.worker.StatsSyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Main Application class for SelfFocus.
 * Initializes Hilt dependency injection and WorkManager background tasks.
 */
@HiltAndroidApp
class SelfFocusApplication : Application() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        initializeWorkManager()
    }

    /**
     * Configura WorkManager para sincronizar estadísticas de uso periódicamente.
     * Ejecuta StatsSyncWorker cada 15 minutos.
     */
    private fun initializeWorkManager() {
        val syncRequest = PeriodicWorkRequestBuilder<StatsSyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "stats_sync_work",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
