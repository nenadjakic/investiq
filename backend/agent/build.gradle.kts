plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.github.nenadjakic.investiq"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven { url = uri("https://repo.spring.io/milestone") }

    maven { url = uri("https://repo.spring.io/snapshot") }

    maven {
        name = "Central Portal Snapshots"
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
}

dependencies {
    implementation(project(":service"))
    implementation(project(":toon"))
    implementation(project(":common"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)

    implementation(libs.spring.ai.starter.model.openai)
    implementation(libs.spring.ai.starter.memory.repository.jdbc)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.junit.jupiter)

    testRuntimeOnly(libs.junit.platformLauncher)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintageEngine)
}

tasks.test {
    useJUnitPlatform()
}
