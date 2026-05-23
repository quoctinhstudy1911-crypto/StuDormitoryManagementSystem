package com.stu.dormitory.modules.application.dto.request;

import com.stu.dormitory.modules.application.enums.VerificationStatus;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyPriorityDocumentRequest {

    @NotNull
    private VerificationStatus status;

    private String note;
}
