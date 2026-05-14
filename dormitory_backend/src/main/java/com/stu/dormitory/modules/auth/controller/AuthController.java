package com.stu.dormitory.modules.auth.controller;

import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.auth.dto.request.ChangePasswordRequest;
import com.stu.dormitory.modules.auth.dto.request.ForgotPasswordRequest;
import com.stu.dormitory.modules.auth.dto.request.LoginRequest;
import com.stu.dormitory.modules.auth.dto.request.RefreshTokenRequest;
import com.stu.dormitory.modules.auth.dto.response.CurrentUserResponse;
import com.stu.dormitory.modules.auth.dto.response.LoginResponse;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ==============================
    // LOGIN
    // ==============================

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authService.login(request);

        return new ApiResponse<>(
                true,
                "Login successful",
                response
        );
    }

    // ==============================
    // LOGOUT
    // ==============================

    @PostMapping("/logout")
    public ApiResponse<?> logout(
            Authentication authentication
    ) {

        UserAccount account =
                (UserAccount)
                        authentication.getPrincipal();

        authService.logout(
                account.getUsername()
        );

        return new ApiResponse<>(
                true,
                "Logout successful",
                null
        );
    }

    // ==============================
    // REFRESH TOKEN
    // ==============================

    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        LoginResponse response =
                authService.refreshToken(request.getRefreshToken());

        return new ApiResponse<>(
                true,
                "Refresh token successful",
                response
        );
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> getMe(
            Authentication authentication
    ) {

        UserAccount account =
                (UserAccount) authentication.getPrincipal();

        CurrentUserResponse response =
                authService.getCurrentUser(account);

        return new ApiResponse<>(
                true,
                "Get current user successfully",
                response
        );
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(
            Authentication authentication,

            @Valid
            @RequestBody
            ChangePasswordRequest request
    ) {

        UserAccount account =
                (UserAccount)
                        authentication.getPrincipal();

        authService.changePassword(
                account,
                request.getOldPassword(),
                request.getNewPassword()
        );

        return new ApiResponse<>(
                true,
                "Password changed successfully",
                null
        );
    }

    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(

            @Valid
            @RequestBody
            ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request);

        return new ApiResponse<>(
                true,
                "New password has been sent to your email",
                null
        );
    }
}