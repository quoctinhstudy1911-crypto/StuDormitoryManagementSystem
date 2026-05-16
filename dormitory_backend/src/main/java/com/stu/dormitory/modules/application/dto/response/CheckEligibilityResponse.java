package com.stu.dormitory.modules.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckEligibilityResponse {

    // Có được đăng ký không
    private Boolean eligible;

    // Đợt đăng ký hiện tại
    private String registrationPeriodName;

    // Loại đối tượng
    private String target;
}