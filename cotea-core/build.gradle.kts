plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    // For build.gradle.kts (Kotlin DSL)
//    kotlin("jvm") version "1.8.20"
}

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.mw.zhalskiy.cotea-core"
            artifactId = "cotea-core"
            version = "0.1"

            from(components["kotlin"])
        }
    }
}
