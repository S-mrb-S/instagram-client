// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath ("com.android.tools.build:gradle:8.5.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}

plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id ("com.google.devtools.ksp") version "2.0.0-1.0.22" apply false
}
