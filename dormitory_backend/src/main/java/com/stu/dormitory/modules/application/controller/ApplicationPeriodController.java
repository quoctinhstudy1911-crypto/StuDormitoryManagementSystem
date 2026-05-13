package com.stu.dormitory.modules.application.controller;

import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.application.entity.ApplicationPeriod;
import com.stu.dormitory.modules.application.service.ApplicationPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/period")
@RequiredArgsConstructor
public class ApplicationPeriodController {

    private final ApplicationPeriodService service;

    @GetMapping("/current")
    public ApiResponse<ApplicationPeriod> getCurrent() {
        return new ApiResponse<>(true, "Thành công", service.getCurrentPeriod());
    }
}
