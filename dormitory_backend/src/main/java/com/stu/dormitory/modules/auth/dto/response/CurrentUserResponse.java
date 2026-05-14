package com.stu.dormitory.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUserResponse {

    private String username;

    private String role;
}