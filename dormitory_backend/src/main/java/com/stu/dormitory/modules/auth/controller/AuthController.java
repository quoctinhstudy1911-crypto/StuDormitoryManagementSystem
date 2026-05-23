package com.stu.dormitory.modules.auth.controller;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.auth.dto.request.ChangePasswordRequest;
import com.stu.dormitory.modules.auth.dto.request.ForgotPasswordRequest;
import com.stu.dormitory.modules.auth.dto.request.LoginRequest;
import com.stu.dormitory.modules.auth.dto.request.RefreshTokenRequest;
import com.stu.dormitory.modules.auth.dto.response.LoginResponse;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * ĐĂNG NHẬP – public endpoint
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return new ApiResponse<>(true, "Login successful", response);
    }

    /**
     * ĐĂNG XUẤT – yêu cầu đã đăng nhập
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        authService.logout();
        return new ApiResponse<>(true, "Logout successful", null);
    }

    /**
     * LÀM MỚI ACCESS TOKEN – public endpoint (dùng refresh token)
     */
    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return new ApiResponse<>(true, "Refresh token successful", response);
    }

    /**
     * ĐỔI MẬT KHẨU
     */
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request.getOldPassword(), request.getNewPassword());
        return new ApiResponse<>(true, "Password changed successfully", null);
    }

    /**
     * QUÊN MẬT KHẨU – public endpoint
     */
    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return new ApiResponse<>(true, "New password has been sent to your email", null);
    }

    // ==============================
    // PRIVATE HELPER
    // ==============================

    /**
     * Trích xuất UserAccount từ Authentication một cách an toàn
     * @throws AppException nếu chưa đăng nhập hoặc principal không đúng kiểu
     */
    private UserAccount extractUserAccount(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserAccount)) {
            throw new AppException("Not authenticated", HttpStatus.UNAUTHORIZED);
        }
        return (UserAccount) authentication.getPrincipal();
    }
}