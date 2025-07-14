# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep payment related classes
-keep class com.caverock.androidsvg.** { *; }
-keep class com.google.android.apps.nbu.paisa.inapp.** { *; }
-keep class com.google.android.gms.auth.api.credentials.** { *; }
-keep class com.payu.** { *; }
-keep class com.razorpay.** { *; }
-keep class kotlinx.parcelize.** { *; }
-keep class proguard.annotation.** { *; }

# Keep payment SDK related classes
-keep class com.cashfree.** { *; }
-keep class com.paytm.** { *; }

# Keep all model classes
-keep class **.models.** { *; }

# Keep all interfaces
-keep interface * {
    <methods>;
}

# Keep all enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep all Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep all Serializable implementations
-keep class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep all native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all WebView related stuff
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

# Keep all JavaScript interfaces
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep ludo game classes
-keep class com.first_player_games.** { *; }
-keep class com.first_player_games.LocalGame.** { *; }
-keep class com.first_player_games.OnlineGame.** { *; }
-keep class com.first_player_games.Menu.** { *; }
-keep class com.first_player_games.Api.** { *; }
