plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)

    alias(libs.plugins.spring.dependency.management)

    jacoco
}

dependencies {
    implementation(project(":data"))
    implementation(project(":currency-fetcher"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.jakarta.transaction.api)
    implementation(libs.spring.data.jpa)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}