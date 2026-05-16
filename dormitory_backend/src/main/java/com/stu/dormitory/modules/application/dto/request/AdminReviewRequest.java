package com.stu.dormitory.modules.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminReviewRequest {

    @NotBlank(message = "Action is required")
    private String action;
    // APPROVE / REJECT / REQUEST_SUPPLEMENT

    private String note;
}