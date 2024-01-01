import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose").version("1.5.10")
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
}

compose.desktop {

    application {

        mainClass = "io.neverdo.jvm.MainKt"

        buildTypes.release {
            proguard {
                configurationFiles.from("proguard_jvm.pro")
            }
        }

        // https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Native_distributions_and_local_execution
        nativeDistributions {
            modules("java.sql")
            packageName = "neverdo"
            packageVersion = "1.0.0"
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            macOS {
                bundleID = "io.neverdo"
                packageName = "neverdo"
            }
        }
    }
}
