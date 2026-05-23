package com.stu.dormitory;

import com.stu.dormitory.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties(JwtConfig.class)
@SpringBootApplication
@EnableScheduling
public class DormitoryBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryBackendApplication.class, args);
    }
}