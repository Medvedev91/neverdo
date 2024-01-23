plugins {
    kotlin("multiplatform")
    id("app.cash.sqldelight").version("2.0.1")
}

kotlin {

    jvm()

    listOf(
        macosX64(),
        macosArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {

        val sqldelight_version = "2.0.1"

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                implementation("app.cash.sqldelight:primitive-adapters:$sqldelight_version")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqldelight_version")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:sqlite-driver:$sqldelight_version")
            }
        }

        val appleMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("app.cash.sqldelight:native-driver:$sqldelight_version")
            }
        }

        val macosX64Main by getting
        val macosArm64Main by getting
        val macosMain by creating {
            dependsOn(commonMain)
            dependsOn(appleMain)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
        }
    }
}


sqldelight {
    databases {
        create("NeverdoDB") {
            packageName.set("io.neverdo.appdbsq")
        }
    }
}
