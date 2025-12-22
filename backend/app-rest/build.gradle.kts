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

configurations {
    developmentOnly
    runtimeClasspath {
        extendsFrom(configurations.developmentOnly.get())
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":common"))
    implementation(project(":service"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.apache.commons.csv)
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)
    runtimeOnly(libs.postgresql)

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation(libs.spring.boot.flyway)
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

openApi {
    apiDocsUrl.set("http://localhost:8080/v3/api-docs")
    outputDir.set(layout.buildDirectory.dir("openapi"))
    outputFileName.set("openapi.json")
    waitTimeInSeconds.set(120)
}