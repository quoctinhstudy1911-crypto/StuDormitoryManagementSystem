package com.stu.dormitory.modules.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequestDTO {

    @NotBlank(message = "CCCD không được để trống")
    private String cccd;

    @NotNull(message = "PeriodId không được null")
    private Long periodId;
}