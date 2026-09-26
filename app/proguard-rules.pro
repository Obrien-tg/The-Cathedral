# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, Exceptions, *Annotation*
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
    @kotlinx.serialization.Transient <fields>;
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
-keep class * implements kotlinx.serialization.KSerializer { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$ViewComponentBuilderEntryPoint { *; }

# DataStore / Proto
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep your model classes
-keep class com.obrien.core.model.** { *; }
-keep class com.obrien.thecathedral.model.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }
