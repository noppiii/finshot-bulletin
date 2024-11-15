package com.example.finshot.bulletin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("Authorization", "accessToken", "Content-Type", "X-Requested-With")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
