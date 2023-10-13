# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep Project Model Classes
-keep public class com.hypersoft.baseproject.commons.models.**

# Keep Parcelabe & Serializable
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

# keep enums
-keepclassmembers enum * { *; }

# android basic
-keepattributes *Annotation*, Signature, Exception
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}
-keep class com.google.android.material.bottomsheet.** { *; }
-keep class com.google.android.datatransport.** { *; }
-keep class com.google.android.material.** { *; }

# lifecycle
-keep class androidx.lifecycle.** {*;}
-keep class android.arch.lifecycle.** {*;}

# App Startup
-keep class androidx.startup.AppInitializer
-keep class * extends androidx.startup.Initializer

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Lottie
-keep class com.airbnb.lottie.** { *; }

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Google Play Services ads
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# If you are using Google Play Services for Firebase, also include these rules
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Google Play Billing Library
-keep class com.android.billingclient.** { *; }
-keepattributes InnerClasses

# Google Play Billing Library 3.x.x
-keep class com.android.billingclient.api.** { *; }

