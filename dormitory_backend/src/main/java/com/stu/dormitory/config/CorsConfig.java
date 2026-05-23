package com.stu.dormitory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình CORS (Cross-Origin Resource Sharing)
 * - Hiện tại cho phép tất cả origin (dành cho môi trường phát triển)
 * - Khi lên PRODUCTION: thay allowedOrigins("*") bằng domain thật của frontend
 *   Ví dụ: .allowedOrigins("https://your-frontend-domain.com")
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // ⚠️ PRODUCTION: cần thay bằng domain cụ thể
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(false) // BẮT BUỘC false khi dùng "*"
                .maxAge(3600);
    }
}