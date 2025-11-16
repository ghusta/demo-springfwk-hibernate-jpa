package org.example.config;

import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

//    @Bean
//    public MappingJackson2HttpMessageConverter jsonConverter() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        // Customize objectMapper as needed
//        return new MappingJackson2HttpMessageConverter(objectMapper);
//    }

    @Bean
    public JacksonJsonHttpMessageConverter jsonConverter() {
        JsonMapper objectMapper = JsonMapper.builder().build();
        // Customize objectMapper as needed
        return new JacksonJsonHttpMessageConverter(objectMapper);
    }

}
