package org.example.userservice.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class GenericConfig {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper(); //CONVERT STRING TO JSON AND VICE VERSA
    }
}
