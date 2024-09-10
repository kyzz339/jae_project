package com.springboot.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOriginPatterns("http://localhost:3000", "http://172.30.1.87:3000") // 두 도메인 허용
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowCredentials(true)
        .allowedHeaders("*");
        
        
    }
}
