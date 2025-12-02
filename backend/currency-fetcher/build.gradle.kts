plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)

    alias(libs.plugins.spring.dependency.management)

    jacoco
}

dependencies {
    implementation(project(":data"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.spring.framework.context)
    implementation(libs.spring.framework.web)
    implementation(libs.spring.data.jpa)

    implementation("tools.jackson.core:jackson-databind:3.0.3")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.3")


    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}