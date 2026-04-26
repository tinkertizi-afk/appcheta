package com.selffocus.core.di

import android.content.Context
import android.content.pm.PackageManager
import android.app.usage.UsageStatsManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Configuration
import androidx.work.WorkManager
import com.selffocus.data.local.AppDatabase
import com.selffocus.data.local.AppUsageDao
import com.selffocus.data.local.LimitDao
import com.selffocus.data.local.SessionDao
import com.selffocus.data.prefs.DataStoreManager
import com.selffocus.data.repository.AppUsageRepositoryImpl
import com.selffocus.data.repository.FocusSessionRepositoryImpl
import com.selffocus.data.repository.LimitRepositoryImpl
import com.selffocus.data.repository.PreferencesRepositoryImpl
import com.selffocus.data.util.UsageStatsHelper
import com.selffocus.domain.repository.AppUsageRepository
import com.selffocus.domain.repository.FocusSessionRepository
import com.selffocus.domain.repository.LimitRepository
import com.selffocus.domain.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PREFS_NAME = "selffocus_prefs"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

/**
 * Dagger module providing singleton dependencies for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideAppUsageDao(database: AppDatabase): AppUsageDao {
        return database.appUsageDao()
    }

    @Provides
    @Singleton
    fun provideLimitDao(database: AppDatabase): LimitDao {
        return database.limitDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(dataStore: DataStore<Preferences>): DataStoreManager {
        return DataStoreManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideUsageStatsManager(@ApplicationContext context: Context): UsageStatsManager {
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideUsageStatsHelper(@ApplicationContext context: Context): UsageStatsHelper {
        return UsageStatsHelper(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAppUsageRepository(
        dao: AppUsageDao,
        usageStatsManager: UsageStatsManager,
        packageManager: PackageManager
    ): AppUsageRepository {
        return AppUsageRepositoryImpl(dao, usageStatsManager, packageManager)
    }

    @Provides
    @Singleton
    fun provideLimitRepository(
        dao: LimitDao,
        dataStoreManager: DataStoreManager
    ): LimitRepository {
        return LimitRepositoryImpl(dao, dataStoreManager)
    }

    @Provides
    @Singleton
    fun provideFocusSessionRepository(
        dao: SessionDao,
        dataStoreManager: DataStoreManager
    ): FocusSessionRepository {
        return FocusSessionRepositoryImpl(dao, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        dataStoreManager: DataStoreManager
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(dataStoreManager)
    }
}
