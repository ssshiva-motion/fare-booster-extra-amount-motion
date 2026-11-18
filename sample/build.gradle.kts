import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvmToolchain(17)
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":motion"))
                implementation(compose.desktop.currentOs)
                implementation(compose.foundation)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.porter.farebooster.sample.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "FareBoosterMotionSample"
            packageVersion = "1.0.0"
        }
    }
}
