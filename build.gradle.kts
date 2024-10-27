import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "io.github.T2PeNBiX99wcoxKv3A4g.ton-sign"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(kotlin("reflect"))
    implementation("com.illposed.osc:javaosc-core:${property("javaosc.version")}")
    // No type handler registered for serializing class java.lang.String
//    implementation("com.illposed.osc:javaosc-java-se-addons:${property("javaosc.version")}")
    implementation("com.charleskorn.kaml:kaml:${property("kaml.version")}")
    implementation("org.slf4j:slf4j-api:${property("slf4j.version")}")
    implementation("ch.qos.logback:logback-core:${property("logback.version")}")
    implementation("ch.qos.logback:logback-classic:${property("logback.version")}")
    implementation("org.codehaus.janino:janino:${property("janino.version")}")
    implementation("org.fusesource.jansi:jansi:${property("jansi.version")}")
    implementation("io.github.oshai:kotlin-logging-jvm:${property("kotlin-logging.version")}")
//    implementation("org.jetbrains.androidx.navigation:navigation-compose:${property("navigation-compose.version")}")
}

compose.desktop {
    application {
        mainClass = "io.github.t2penbix99wcoxkv3a4g.tonsign.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.naming")
            packageName = "TON-Sign"
            packageVersion = "1.0.0"
        }
    }
}
