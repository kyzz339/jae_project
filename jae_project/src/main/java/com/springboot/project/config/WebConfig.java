package com.springboot.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Value("${uploadDir}") 
    private String uploadDir;

    @Value("${frountIp}") 
    private String frountIp;

    @Value("${frountPort}")
    private String frountPort;
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	
        registry.addMapping("/**")
        //.allowedOriginPatterns("http://localhost:3000", "http://172.30.1.87:3000") // 두 도메인 허용
        .allowedOriginPatterns(frountIp , frountIp +":"+frountPort)
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowCredentials(true)
        .allowedHeaders("*");   
    }
    
    //client 접근경로 조정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
