package com.github.nenadjakic.investiq.restapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.github.nenadjakic.investiq.data.repository"])
@EntityScan(basePackages = ["com.github.nenadjakic.investiq.data.entity"])
@EnableCaching
@EnableScheduling
@ComponentScan(basePackages = ["com.github.nenadjakic.investiq"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}