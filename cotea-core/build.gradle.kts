plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    // For build.gradle.kts (Kotlin DSL)
//    kotlin("jvm") version "1.8.20"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
}
