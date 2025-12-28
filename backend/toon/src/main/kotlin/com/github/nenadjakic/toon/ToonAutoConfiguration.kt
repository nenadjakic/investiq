package com.github.nenadjakic.toon

import com.github.nenadjakic.toon.converter.ToonHttpMessageConverter
import com.github.nenadjakic.toon.serializer.ReflectionToonSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.HttpMessageConverter

@AutoConfiguration
class ToonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun toonSerializer(@Value("\${toon.delimiter:,}") delimiter: String): ReflectionToonSerializer =
        ReflectionToonSerializer(delimiter)

    @Bean
    @ConditionalOnMissingBean
    fun toonHttpMessageConverter(serializer: ReflectionToonSerializer): HttpMessageConverter<Any> =
        ToonHttpMessageConverter(serializer)

}
