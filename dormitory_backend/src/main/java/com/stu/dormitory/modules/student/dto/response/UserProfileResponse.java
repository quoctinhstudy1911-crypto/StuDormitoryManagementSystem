package com.stu.dormitory.modules.student.dto.response;

import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.auth.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserProfileResponse {

    private Long id;

    private String username;

    private Role role;

    private String studentCode;

    private String fullName;

    private String cccd;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String phone;

    private String email;

    private String faculty;

    private String course;

    private String avatarUrl;
}
