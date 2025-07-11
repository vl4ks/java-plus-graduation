package ru.practicum.clients;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatClientFeignConfig {
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public RequestInterceptor statClientInterceptor() {
        return requestTemplate -> requestTemplate.header("X-App-Name", appName);
    }
}
