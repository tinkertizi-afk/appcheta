package com.selffocus.data.util

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import com.selffocus.domain.model.AppUsageRecord
import java.util.Calendar

/**
 * Helper para obtener estadísticas de uso de aplicaciones del sistema.
 * Requiere el permiso PACKAGE_USAGE_STATS concedido manualmente por el usuario.
 */
class UsageStatsHelper(private val context: Context) {

    private val usageStatsManager = 
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager

    /**
     * Obtiene las estadísticas de uso para el día actual.
     * @return Lista de registros de uso por aplicación
     */
    fun getDailyUsageStats(): List<AppUsageRecord> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        
        // Obtener estadísticas del sistema
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()
        
        // Mapear a dominio
        return usageStatsList
            .filter { it.totalTimeInForeground > 0 }
            .map { stats ->
                val appName = getAppName(stats.packageName)
                AppUsageRecord(
                    packageName = stats.packageName,
                    appName = appName,
                    totalTimeInForeground = stats.totalTimeInForeground,
                    lastTimeUsed = stats.lastTimeUsed,
                    timestamp = System.currentTimeMillis()
                )
            }
            .sortedByDescending { it.totalTimeInForeground }
    }

    /**
     * Obtiene el nombre legible de una aplicación desde su package name.
     */
    private fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    /**
     * Verifica si la app tiene permiso de acceso a estadísticas de uso.
     */
    fun hasUsageAccessPermission(): Boolean {
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            System.currentTimeMillis() - 1000,
            System.currentTimeMillis()
        )?.isNotEmpty() == true
    }
}
