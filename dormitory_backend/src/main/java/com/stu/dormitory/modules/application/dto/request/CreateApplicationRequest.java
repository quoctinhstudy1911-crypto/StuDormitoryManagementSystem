package com.stu.dormitory.modules.application.dto.request;

import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.application.enums.PriorityCategory;
import com.stu.dormitory.modules.application.enums.RegistrationType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateApplicationRequest {

    @NotBlank(message = "CCCD is required")
    private String cccd;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "gender is required")
    private Gender gender;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    // Nullable với tân sinh viên
    private String studentCode;

    private String note;

    @NotNull(message = "Registration type is required")
    private RegistrationType registrationType;

    private List<PriorityCategory> priorityCategories;
}