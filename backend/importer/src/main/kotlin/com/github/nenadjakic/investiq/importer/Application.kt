package com.github.nenadjakic.investiq.importer

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.github.nenadjakic.investiq.data.repository"])
@EntityScan(basePackages = ["com.github.nenadjakic.investiq.data.entity"])
class Application {
    @Bean
    fun flywayMigrate(dataSource: DataSource): Flyway {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:/migrations")
            .baselineOnMigrate(true)
            .load()
        //flyway.migrate()
        return flyway
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}