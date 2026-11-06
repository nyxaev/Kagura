# Kagura Phase 2 ProGuard Rules

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers enum * { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# kotlinx.serialization
-keepattributes *Annotation*, ElementValueAttribute
-keepnames class kotlinx.serialization.protobuf.** { *** a(...); }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Hilt
-keep class androidx.hilt.work.** { *; }
-keepclassmembers class * {
    @dagger.hilt.** *;
}
