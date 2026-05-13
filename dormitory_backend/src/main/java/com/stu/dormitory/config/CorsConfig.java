package com.stu.dormitory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig - Cấu hình CORS cho backend
 *
 * Cho phép frontend trên:
 * - http://localhost:5173 (Vite default)
 * - http://localhost:5174 (Vite custom)
 * - http://localhost:3000 (Next.js)
 *
 * Cho phép tất cả methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
 * Cho phép tất cả headers
 * Cache CORS setting trong 3600 giây
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173", "http://localhost:5174")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
