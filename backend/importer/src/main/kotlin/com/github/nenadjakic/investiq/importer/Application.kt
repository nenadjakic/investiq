package com.github.nenadjakic.investiq.importer

import org.flywaydb.core.Flyway
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.shell.jline.PromptProvider
import javax.sql.DataSource


@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.github.nenadjakic.investiq.data.repository"])
@EntityScan(basePackages = ["com.github.nenadjakic.investiq.data.entity"])
@EnableCaching
class Application: PromptProvider  {

    override fun getPrompt(): AttributedString? {
        return AttributedString("importer:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
    }

    @Bean
    fun flywayMigrate(
        dataSource: DataSource,
        env: Environment
    ): Flyway {

        val enabled = env.getProperty("spring.flyway.enabled", Boolean::class.java, false)

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations(env.getProperty("spring.flyway.locations", "classpath:/migrations"))
            .schemas(env.getProperty("spring.flyway.schemas", "public"))
            .baselineOnMigrate(
                env.getProperty("spring.flyway.baseline-on-migrate", Boolean::class.java, true)
            )
            .load()

        if (enabled) {
            flyway.migrate()
        }

        return flyway
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}