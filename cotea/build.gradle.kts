import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-library")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<KotlinJvmCompile>("compileKotlin"){
    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
    }
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
            from(components["kotlin"])
        }
    }
}