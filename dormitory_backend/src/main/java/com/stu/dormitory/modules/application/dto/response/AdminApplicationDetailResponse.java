package com.stu.dormitory.modules.application.dto.response;

import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.application.enums.RegistrationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminApplicationDetailResponse {

    private String applicationCode;

    private String cccd;

    private String fullName;

    private Gender gender;

    private String email;

    private String phone;

    private String studentCode;

    private String note;

    private Integer priorityScore;

    private RegistrationType registrationType;

    private ApplicationStatus status;

    private String applicationPdfUrl;

    private LocalDateTime submittedAt;

    private LocalDateTime paymentDeadline;

    private LocalDateTime revisionDeadline;
}