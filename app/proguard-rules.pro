# ---- Journii security rules ----

# Strip verbose/debug logging from release builds so nothing sensitive
# (tokens, user data, request bodies) ever ends up in logcat in production.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Keep stack traces useful for crash reports without exposing real file paths.
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Coroutines / annotations used across the app
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.coroutines.**

# Reserve this package for domain/data models — keep them from being stripped
# once serialization (Room/Retrofit/kotlinx.serialization) lands in a later batch.
-keep class com.example.journii_version2.core.model.** { *; }