package com.stu.dormitory;

import com.stu.dormitory.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DormitoryBackendApplication - Main entry point
 *
 * @EnableScheduling: Kích hoạt scheduled tasks (ví dụ: check expiration mỗi 30 phút)
 *
 * Port: 8080
 * Context path: /api
 */
@EnableConfigurationProperties(JwtConfig.class)
@SpringBootApplication
@EnableScheduling
public class DormitoryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryBackendApplication.class, args);
    }

}
