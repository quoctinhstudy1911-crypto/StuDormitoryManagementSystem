package com.stu.dormitory.modules.application.dto.response;

import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.application.enums.RegistrationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplicationResponse {

    private String applicationCode;

    private String fullName;

    private String cccd;

    private String email;

    private String phone;

    private Gender gender;

    private String studentCode;

    private RegistrationType registrationType;

    private ApplicationStatus status;

    private String applicationPdfUrl;
}