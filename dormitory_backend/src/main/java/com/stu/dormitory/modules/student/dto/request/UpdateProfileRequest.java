package com.stu.dormitory.modules.student.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    // Không bắt buộc, nếu có thì phải không rỗng
    private String fullName;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})", message = "Invalid phone number")
    private String phone;

    @Email(message = "Invalid email")
    private String email;
}