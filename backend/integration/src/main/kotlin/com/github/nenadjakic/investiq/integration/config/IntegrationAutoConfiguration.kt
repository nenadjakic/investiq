package com.github.nenadjakic.investiq.integration.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import tools.jackson.databind.ObjectMapper


@AutoConfiguration
@ConditionalOnClass(RestTemplate::class, ObjectMapper::class)
class IntegrationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}