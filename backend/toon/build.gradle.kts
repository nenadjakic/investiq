plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.spring.boot.autoconfigure)
    implementation(libs.spring.framework.web)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.platformLauncher)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintageEngine)
}