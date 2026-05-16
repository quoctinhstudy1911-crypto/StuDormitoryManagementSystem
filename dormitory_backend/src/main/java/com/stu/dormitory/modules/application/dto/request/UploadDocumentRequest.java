package com.stu.dormitory.modules.application.dto.request;

import com.stu.dormitory.modules.application.enums.VerificationDocumentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadDocumentRequest {

    @NotBlank(message = "Application code is required")
    private String applicationCode;

    @NotNull(message = "Document type is required")
    private VerificationDocumentType documentType;

    @NotBlank(message = "File URL is required")
    private String fileUrl;
}