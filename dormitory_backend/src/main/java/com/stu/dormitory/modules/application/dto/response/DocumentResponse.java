package com.stu.dormitory.modules.application.dto.response;

import com.stu.dormitory.modules.application.enums.VerificationDocumentType;
import com.stu.dormitory.modules.application.enums.VerificationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentResponse {

    private Long id;

    private VerificationDocumentType documentType;

    private String fileUrl;

    private VerificationStatus verificationStatus;
}