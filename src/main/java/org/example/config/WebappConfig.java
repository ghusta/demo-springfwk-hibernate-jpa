package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"org.example.controller", "org.example.exceptionhandling"})
@EnableWebMvc
@Import({BackendConfig.class, JacksonConfig.class})
public class WebappConfig {

}
