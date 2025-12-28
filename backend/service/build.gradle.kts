plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)

    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":data"))
    implementation(project(":common"))
    implementation(project(":integration"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.jakarta.transaction.api)
    implementation(libs.spring.data.jpa)
    implementation(libs.hibernate.core)

    implementation(libs.apache.commons.csv)
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.junit.jupiter)

    testRuntimeOnly(libs.junit.platformLauncher)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintageEngine)
}
