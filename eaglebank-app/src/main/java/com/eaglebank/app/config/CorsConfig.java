package com.eaglebank.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow Swagger UI (running on any port) and common local frontends to call our API
        // Issue 3: Temporary permissive CORS to enable Docker-hosted OpenAPI UI
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8082", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }
}
