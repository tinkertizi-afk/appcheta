package com.selffocus.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.selffocus.R
import com.selffocus.core.constants.AppConstants
import com.selffocus.core.constants.Routes
import com.selffocus.domain.usecase.FetchUsageStats
import com.selffocus.domain.usecase.GetAllLimits
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Worker que sincroniza las estadísticas de uso en segundo plano.
 * Se ejecuta periódicamente cada 15 minutos o cuando hay una sesión Focus activa.
 */
@HiltWorker
class StatsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchUsageStats: FetchUsageStats,
    private val getAllLimits: GetAllLimits
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = 
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        return try {
            // Ejecutar fetch de estadísticas
            fetchUsageStats()
            
            // Verificar límites y mostrar notificaciones si es necesario
            checkLimitsAndNotify()
            
            Result.success()
        } catch (e: Exception) {
            // Reintentar en caso de error
            Result.retry()
        }
    }

    /**
     * Verifica el uso actual contra los límites establecidos y muestra
     * notificaciones suaves cuando se alcanza un límite.
     */
    private suspend fun checkLimitsAndNotify() {
        val limits = getAllLimits().first()
        val usageStats = fetchUsageStats.invokeRaw()
        
        for (limit in limits) {
            val usage = usageStats.find { it.packageName == limit.packageName } ?: continue
            
            if (usage.totalTimeInForeground >= limit.limitMinutes * 60 * 1000L) {
                showLimitReachedNotification(limit.packageName, limit.appName ?: limit.packageName)
            }
        }
    }

    /**
     * Muestra una notificación suave cuando se alcanza un límite de uso.
     */
    private fun showLimitReachedNotification(packageName: String, appName: String) {
        createNotificationChannel()
        
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", Routes.LIMITS)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, AppConstants.NOTIFICATION_CHANNEL_LIMITS)
            .setSmallIcon(R.drawable.ic_selffocus)
            .setContentTitle("⏱️ Límite alcanzado")
            .setContentText("Has usado $appName por el tiempo establecido hoy.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(packageName.hashCode(), notification)
    }

    /**
     * Crea el canal de notificación para alertas de límites.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstants.NOTIFICATION_CHANNEL_LIMITS,
                "Límites de Uso",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones suaves cuando alcanzas tus límites de uso diarios"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
