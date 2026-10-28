# Proguard / R8 configuration for Cafe Coffee App

# Keep data models
-keep class com.example.cafecoffeeapp.domain.model.** { *; }
-keep class com.example.cafecoffeeapp.data.local.entity.** { *; }
-keep class com.example.cafecoffeeapp.data.local.dao.** { *; }

# Keep Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep Firebase Firestore
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
