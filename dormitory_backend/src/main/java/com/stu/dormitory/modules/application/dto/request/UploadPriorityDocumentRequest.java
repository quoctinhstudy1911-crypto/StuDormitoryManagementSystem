package com.stu.dormitory.modules.application.dto.request;

import com.stu.dormitory.modules.application.enums.PriorityDocumentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadPriorityDocumentRequest {

    @NotBlank
    private String applicationCode;

    @NotNull
    private PriorityDocumentType documentType;

    @NotBlank
    private String fileUrl;
}