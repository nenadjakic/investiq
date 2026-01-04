package com.github.nenadjakic.investiq.agent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.github.nenadjakic.investiq.data.repository"])
@EntityScan(basePackages = ["com.github.nenadjakic.investiq.data.entity"])
@ComponentScan(basePackages = ["com.github.nenadjakic.investiq", "com.github.nenadjakic.toon"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
