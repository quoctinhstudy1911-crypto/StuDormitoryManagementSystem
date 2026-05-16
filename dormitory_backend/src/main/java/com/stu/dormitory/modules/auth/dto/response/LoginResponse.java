package com.stu.dormitory.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor // Tất cả các contructor của nó
@Getter
@Setter
public class LoginResponse {
    private String accessToken;

    private String refreshToken;

    private String role;
}
