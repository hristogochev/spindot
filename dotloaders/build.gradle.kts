@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    namespace = "com.hristogochev.dotloaders"
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
    buildFeatures{
        compose=true
    }
    composeOptions{
        kotlinCompilerExtensionVersion="1.4.0"
    }
}


dependencies {
    implementation("androidx.compose.ui:ui:1.3.3")
    implementation("androidx.compose.material:material:1.3.1")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.hristogochev"
                artifactId = "dotloaders"
                version = "1.0.0"

                from(components["release"])
            }
        }
    }
}

