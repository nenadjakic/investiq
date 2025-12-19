import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
}

allprojects {
    group = "com.github.nenadjakic.investiq"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone/") }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")

    java {
        sourceCompatibility = JavaVersion.VERSION_24
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_24)
            languageVersion.set(KotlinVersion.KOTLIN_2_2)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.withType<Test>())
        reports {
            xml.required = false
            csv.required = false
            html.required = true
        }
    }

    tasks.withType<JacocoCoverageVerification> {
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.0".toBigDecimal()
                }
            }
        }
    }

    tasks.named("check") {
        dependsOn(tasks.withType<JacocoCoverageVerification>())
    }
}
