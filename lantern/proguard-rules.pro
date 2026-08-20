# Keep DataStore and Kotlin Serialization classes
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# Keep Kotlinx Serialization
-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *; }

# Keep model classes for serialization
-keep class com.obrien.thelantern.model.** { *; }
