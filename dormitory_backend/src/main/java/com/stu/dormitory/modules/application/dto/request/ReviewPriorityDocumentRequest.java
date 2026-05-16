package com.stu.dormitory.modules.application.dto.request;

import com.stu.dormitory.modules.application.enums.VerificationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewPriorityDocumentRequest {

    @NotBlank
    private VerificationStatus verificationStatus;

    private String rejectReason;
}

/*
    Ở đây thì admin phải gửi thông tin như này
    {
        "verificationStatus": "INVALID"
        "rejectReason": " CCCD mờ "
    }
 */