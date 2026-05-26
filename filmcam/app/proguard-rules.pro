# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep Compose related classes
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep CameraX classes
-keep class androidx.camera.** { *; }
-keepclassmembers class androidx.camera.** { *; }

# Keep model classes
-keep class com.filmcam.app.domain.** { *; }
-keepclassmembers class com.filmcam.app.domain.** { *; }
