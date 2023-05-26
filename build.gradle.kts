// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0-beta03" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.android.library") version "8.1.0-beta03" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.21" apply false

    `maven-publish`
}

//allprojects {
//    repositories {
//        mavenCentral()
//    }
//
//    group = "com.mw.cotea"
//    version = "0.1"
//}