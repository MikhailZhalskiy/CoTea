plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-library")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.test.kotlin)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.turbine)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.MikhailZhalskiy"
            artifactId = "cotea"
            version = "0.1"

            from(components["kotlin"])
        }
    }
}