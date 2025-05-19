-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses
-keepattributes Signature
-keepattributes Subscribe
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }

# We're excluding Material 2 from the project as we're using Material 3
-dontwarn androidx.compose.material.**

# Kotlinx coroutines rules seems to be outdated with the latest version of Kotlin and Proguard
-keep class kotlinx.coroutines.** { *; }
-keep class androidx.datastore.preferences.** { *; }
-keep class io.ktor.* { *; }
-keep class coil3.* { *; }
-keep class ui.navigation.* { *; }
-keepclasseswithmembernames class androidx.compose.foundation.text.** { *; }
-keepclasseswithmembers public class io.github.t2penbix99wcoxkv3a4g.tonsign.MainKt {
    public static void main();
}
-dontwarn kotlinx.coroutines.debug.*
  
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
  
-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
}
#-assumenosideeffects class java.util.Objects {
#public static requireNonNull(...);
#}
#-assumenosideeffects interface org.slf4j.Logger {
#public void trace(...);
#public void debug(...);
#public void info(...);
#public void warn(...);
#public void error(...);
#
#public boolean isTraceEnabled(...);
#public boolean isDebugEnabled(...);
#public boolean isWarnEnabled(...);
#}
#-libraryjars jakarta
#-assumenosideeffects class org.slf4j.LoggerFactory {
#public static getLogger(...);
#}
-dontwarn org.slf4j.*