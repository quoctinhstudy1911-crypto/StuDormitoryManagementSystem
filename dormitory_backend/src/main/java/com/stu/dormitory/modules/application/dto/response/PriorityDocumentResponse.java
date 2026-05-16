package com.stu.dormitory.modules.application.dto.response;

import com.stu.dormitory.modules.application.enums.PriorityDocumentType;
import com.stu.dormitory.modules.application.enums.VerificationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PriorityDocumentResponse {

    private Long id;

    private PriorityDocumentType documentType;

    private String fileUrl;

    private VerificationStatus verificationStatus;

    private String rejectReason;
}