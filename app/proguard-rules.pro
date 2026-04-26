# SelfFocus ProGuard Rules
# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep WorkManager
-keep class * extends androidx.work.Worker { *; }

# Keep your entities, DAOs, and ViewModels
-keep class com.selffocus.data.local.entities.** { *; }
-keep class com.selffocus.data.local.dao.** { *; }
-keep class com.selffocus.domain.model.** { *; }

# Prevent obfuscation of model classes used with serialization
-keep class com.selffocus.domain.model.** { *; }
-keepclassmembers class com.selffocus.domain.model.** { *; }
