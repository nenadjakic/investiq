plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.dependency.management)

    jacoco
}

dependencies {
    implementation(project(":data"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}