package org.example.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration(proxyBeanMethods = false)
class JacksonConfig {
    @Bean
    fun jsonConverter(): MappingJackson2HttpMessageConverter {
        val objectMapper: ObjectMapper = ObjectMapper()
        // Customize objectMapper as needed
        return MappingJackson2HttpMessageConverter(objectMapper)
    }
}
