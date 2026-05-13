package com.stu.dormitory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DormitoryBackendApplication - Main entry point
 *
 * @EnableScheduling: Kích hoạt scheduled tasks (ví dụ: check expiration mỗi 30 phút)
 *
 * Port: 8080
 * Context path: /api
 */
@SpringBootApplication
@EnableScheduling
public class DormitoryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryBackendApplication.class, args);
    }

}
