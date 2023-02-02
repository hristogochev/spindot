buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
    }
}
plugins {
    id("com.android.library") version "7.4.0" apply false
    id("com.android.application") version "7.4.0" apply false
    kotlin("android") version "1.8.0" apply false
}