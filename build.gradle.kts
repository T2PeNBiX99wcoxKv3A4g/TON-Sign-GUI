
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.*

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("app.cash.sqldelight")
}

group = "io.github.t2penbix99wcoxkv3a4g.tonsign"
version = property("ton-sign.version")!!

val generatedVersionDir = "${layout.buildDirectory.asFile.get()}/generated-version"
val generatedLocalizationDir = "${layout.buildDirectory.asFile.get()}/generated-localization"

fun String.sha256() = hashString(this, "SHA-256")

// https://gist.github.com/lovubuntu/164b6b9021f5ba54cefc67f60f7a1a25
private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold(StringBuilder()) { sb, it -> sb.append("%02x".format(it)) }.toString()
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs) {
        exclude(compose.material)
    }
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.androidx.navigation:navigation-compose:${property("navigation-compose.version")}")
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("kotlinx-coroutines.version")}")
    
    implementation("com.illposed.osc:javaosc-core:${property("javaosc.version")}")
    // No type handler registered for serializing class java.lang.String
//    implementation("com.illposed.osc:javaosc-java-se-addons:${property("javaosc.version")}")
    
    implementation("com.charleskorn.kaml:kaml:${property("kaml.version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("kotlinx-serialization-json.version")}")
    
    implementation("org.slf4j:slf4j-api:${property("slf4j.version")}")
    implementation("ch.qos.logback:logback-core:${property("logback.version")}")
    implementation("ch.qos.logback:logback-classic:${property("logback.version")}")
    implementation("io.github.oshai:kotlin-logging-jvm:${property("kotlin-logging.version")}")
    
    implementation("org.codehaus.janino:janino:${property("janino.version")}")
    implementation("org.fusesource.jansi:jansi:${property("jansi.version")}")
//    implementation("javassist:javassist:${property("javassist.version")}")
    
//    implementation("io.github.kdroidfilter:composenativetray:${property("composenativetray.version")}")
    
    implementation("com.github.vrchatapi:vrchatapi-java:${property("vrchatapi.version")}")
    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:${property("okhttp.version")}"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    implementation("app.cash.sqldelight:sqlite-driver:${property("sqldelight.version")}")
    implementation("app.cash.sqldelight:primitive-adapters:${property("sqldelight.version")}")
}

sourceSets {
    main {
        kotlin {
            output.dir(generatedVersionDir)
            output.dir(generatedLocalizationDir)
        }
    }
}

// https://ao.nmoe/questions/50117966/get-version-in-kotlin-java-code-in-gradle-project
tasks.register("generateVersionProperties") {
    doLast {
        val propertiesFile = file("$generatedVersionDir/version.properties")
        propertiesFile.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", "$version")
        val out = FileOutputStream(propertiesFile)
        properties.store(out, null)
    }
}

val localizationDir = "localization"

tasks.register("generateLocalizationFolder") {
    doLast {
        val localization = file("./$localizationDir")
        val localizationCopyTo = file("$generatedLocalizationDir/$localizationDir")

        if (localizationCopyTo.exists()) {
            when {
                localizationCopyTo.isFile -> localizationCopyTo.delete()
                localizationCopyTo.isDirectory -> {
                    localizationCopyTo.listFiles()?.forEach {
                        it.delete()
                    }
                }
            }
            localizationCopyTo.delete()
        }

        localizationCopyTo.parentFile.mkdirs()
        localization.copyTo(localizationCopyTo, true)

        localization.listFiles { _, filename -> filename.endsWith(".yml") }?.forEach {
            it.copyTo(file("${localizationCopyTo.path}/${it.name}"), true)

            val shaFile = file("${localizationCopyTo.path}/${it.name}.sha256")
            val sha256Text = it.readText().sha256()
            shaFile.createNewFile()
            shaFile.writeText("$sha256Text ${it.name}")
        }
    }
}

tasks.named("processResources") {
    dependsOn("generateVersionProperties")
    dependsOn("generateLanguageFolder")
}

compose.resources {
    publicResClass = false
    packageOfResClass = "$group.resources"
    generateResClass = auto
}

sqldelight {
    databases {
        create("Save") {
            packageName.set("io.github.t2penbix99wcoxkv3a4g.tonsign.data")
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.t2penbix99wcoxkv3a4g.tonsign.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.naming")
            modules("java.sql")
            packageName = "TON-Sign"
            packageVersion = "1.0.0"
        }

        buildTypes.release.proguard {
            version.set(property("proguard.version") as String)
            configurationFiles.from("proguard.pro")
        }
    }
}
