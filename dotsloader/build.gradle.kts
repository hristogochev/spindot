@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    namespace = "com.agrawalsuneet.dotsloader"
    this.compileSdkVersion = "android-33"

    defaultConfig {
        minSdk = 21
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.hristogochev"
                artifactId = "dotsloader"
                version = "1.5.0"

                from(components["release"])
            }
        }
    }
}

