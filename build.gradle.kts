import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "io.github.T2PeNBiX99wcoxKv3A4g.ton-sign"
version = "1.0-SNAPSHOT"

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
    implementation("com.illposed.osc:javaosc-core:${property("javaosc.version")}")
    implementation("com.charleskorn.kaml:kaml:${property("kaml.version")}")
    implementation("org.slf4j:slf4j-simple:${property("slf4j.version")}")
    implementation("io.github.oshai:kotlin-logging-jvm:${property("kotlin-logging.version")}")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TON-Sign"
            packageVersion = "1.0.0"
        }
    }
}
