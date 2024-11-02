-keep class androidx.compose.runtime.** { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.ui.text.platform.** { *; }
-keep class ch.qos.logback.** { *; }
-keep class ch.qos.cal10n.** { *; }
-keep class org.codehaus.janino.** { *; }
-keep class javassist.** { *; }
#-keep class jakarta.mail.** { *; }
#-keep class jakarta.servlet.** { *; }
-keep class com.sun.jna.** { *; }
-keep class com.kdroid.composetray.** { *; }
-keep class io.github.t2penbix99wcoxkv3a4g.tonsign.** { *; } # Hello??? WTF?
-keep class com.illposed.osc.argument.handler.** { *; }
-keep class java.sql.** { *; }

# We're excluding Material 2 from the project as we're using Material 3
-dontwarn androidx.compose.material.**
-dontwarn org.codehaus.janino.**
-dontwarn ch.qos.logback.core.net.**
-dontwarn ch.qos.logback.core.status.**
-dontwarn ch.qos.logback.classic.servlet.**
-dontwarn ch.qos.logback.classic.selector.servlet.**
-dontwarn ch.qos.logback.classic.helpers.**
-dontwarn ch.qos.logback.classic.ViewStatusMessagesServlet
-dontwarn org.slf4j.cal10n.**
-dontwarn io.github.oshai.kotlinlogging.coroutines.**
-dontwarn org.codehaus.commons.compiler.samples.**

# Kotlinx coroutines rules seems to be outdated with the latest version of Kotlin and Proguard
-keep class kotlinx.coroutines.** { *; }
