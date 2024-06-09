package com.example.notificationmicroservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean //tells springboot to create a bean of the return for this method
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //the rest template return bean can now be autowired and used
}
