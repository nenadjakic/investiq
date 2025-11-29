plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.openapi.gradle)

    application
    jacoco
}

dependencies {
    implementation(project(":data"))
    implementation(project(":currency-fetcher"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    //implementation(libs.spring.boot.starter)
    implementation(libs.spring.shell.starter)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.cache)
    //implementation(libs.spring.boot.starter.web)
    //implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.apache.commons.csv)
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)
    runtimeOnly(libs.postgresql)

    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.spring.boot.starter.test)
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    standardInput = System.`in`
}
tasks.test {
    useJUnitPlatform()
}