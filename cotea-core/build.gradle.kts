plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-library")
    `maven-publish`
}
//plugins {
//    id("java-library")
//    id("org.jetbrains.kotlin.jvm")
//    `maven-publish`
//}

//java {
//    sourceCompatibility = JavaVersion.VERSION_17
//    targetCompatibility = JavaVersion.VERSION_17
//}

dependencies {
    implementation(libs.coroutines)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.MikhailZhalskiy"
            artifactId = "cotea-core"
            version = "0.1"

            from(components["kotlin"])
        }
    }
}
