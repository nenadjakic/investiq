package com.github.nenadjakic.investiq.agent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import com.github.nenadjakic.investiq.agent.service.PortfolioChatService
import com.github.nenadjakic.investiq.agent.tool.AgentTool
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.github.nenadjakic.investiq.data.repository"])
@EntityScan(basePackages = ["com.github.nenadjakic.investiq.data.entity"])
@ComponentScan(basePackages = ["com.github.nenadjakic.investiq", "com.github.nenadjakic.toon"])
class Application {

    @Bean
    fun runAtStartup(portfolioChatService: PortfolioChatService, agentTool: AgentTool) = CommandLineRunner {
        // Try AI-backed analysis; if ChatClient isn't configured or fails, fall back to the local analysis
        val analysis = try {
            var toon = agentTool.getPortfolioHoldings()
            portfolioChatService.analyzeWithAi()
        } catch (ex: Exception) {

        }

        println(analysis)
    }

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
