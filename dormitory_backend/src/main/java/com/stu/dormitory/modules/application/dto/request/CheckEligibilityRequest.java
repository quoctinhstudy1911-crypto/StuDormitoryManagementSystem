package com.stu.dormitory.modules.application.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckEligibilityRequest {

    @NotBlank(message = "CCCD is required")
    private String cccd;
}