package com.stu.dormitory.test;

import com.stu.dormitory.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public ApiResponse<?> test() {

        return new ApiResponse<>(
                true,
                "JWT works successfully",
                null
        );
    }
}