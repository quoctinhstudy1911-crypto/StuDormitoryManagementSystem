package com.stu.dormitory.modules.auth.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.auth.dto.request.ForgotPasswordRequest;
import com.stu.dormitory.modules.auth.dto.request.LoginRequest;
import com.stu.dormitory.modules.auth.dto.response.CurrentUserResponse;
import com.stu.dormitory.modules.auth.dto.response.LoginResponse;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.stu.dormitory.common.service.EmailService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    // ==============================
    // DEPENDENCIES
    // ==============================

    private final UserAccountRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final EmailService emailService;

    // ==============================
    // LOGIN
    // ==============================

    public LoginResponse login(LoginRequest request) {

        // 1. FIND ACCOUNT
        UserAccount account = repository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new AppException(
                                "Invalid username or password",
                                HttpStatus.UNAUTHORIZED
                        ));

        // 2. CHECK SOFT DELETE
        if (account.getIsDeleted()) {

            throw new AppException(
                    "Account has been deleted",
                    HttpStatus.FORBIDDEN
            );
        }

        // 3. CHECK ACTIVE
        if (!account.getIsActive()) {

            throw new AppException(
                    "Account is inactive",
                    HttpStatus.FORBIDDEN
            );
        }

        // 4. CHECK PASSWORD
        boolean isMatch = passwordEncoder.matches(
                request.getPassword(),
                account.getPassword()
        );

        if (!isMatch) {

            throw new AppException(
                    "Invalid username or password",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 5. GENERATE TOKENS
        String accessToken =
                jwtService.generateAccessToken(account);

        String refreshToken =
                jwtService.generateRefreshToken(account);

        // 6. SAVE REFRESH TOKEN
        account.setRefreshToken(refreshToken);

        // 7. UPDATE LAST LOGIN
        account.setLastLogin(LocalDateTime.now());

        // 8. SAVE ACCOUNT
        repository.save(account);

        // 9. RESPONSE
        return new LoginResponse(
                accessToken,
                refreshToken,
                account.getRole().name()
        );
    }

    // ==============================
    // LOGOUT
    // ==============================

    public void logout(String username) {

        UserAccount account = repository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException(
                                "Account not found",
                                HttpStatus.NOT_FOUND
                        ));

        // CLEAR REFRESH TOKEN
        account.setRefreshToken(null);

        repository.save(account);
    }

    // ==============================
    // REFRESH ACCESS TOKEN
    // ==============================

    public LoginResponse refreshToken(String refreshToken) {

        // 1. VALIDATE REFRESH TOKEN
        jwtService.validateRefreshToken(refreshToken);

        // 2. EXTRACT USERNAME
        String username =
                jwtService.extractUsernameFromRefreshToken(refreshToken);

        // 3. FIND ACCOUNT
        UserAccount account = repository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException(
                                "Account not found",
                                HttpStatus.NOT_FOUND
                        ));

        // 4. CHECK REFRESH TOKEN MATCH
        if (
                account.getRefreshToken() == null
                        ||
                        !account.getRefreshToken().equals(refreshToken)
        ) {

            throw new AppException(
                    "Invalid refresh token",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 5. GENERATE NEW ACCESS TOKEN
        String newAccessToken =
                jwtService.generateAccessToken(account);

        // 6. GENERATE NEW REFRESH TOKEN
        String newRefreshToken =
                jwtService.generateRefreshToken(account);

        // 7. SAVE NEW REFRESH TOKEN
        account.setRefreshToken(newRefreshToken);

        repository.save(account);

        // 8. RETURN RESPONSE
        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                account.getRole().name()
        );
    }

    // ==============================
    // CHANGE PASSWORD
    // ==============================

    public void changePassword(
            UserAccount account,
            String oldPassword,
            String newPassword
    ) {

        // 1. CHECK OLD PASSWORD
        boolean isMatch = passwordEncoder.matches(
                oldPassword,
                account.getPassword()
        );

        if (!isMatch) {

            throw new AppException(
                    "Old password is incorrect",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 2. ENCODE NEW PASSWORD
        String encodedPassword =
                passwordEncoder.encode(newPassword);

        // 3. UPDATE PASSWORD
        account.setPassword(encodedPassword);

        // 4. CLEAR REFRESH TOKEN
        account.setRefreshToken(null);

        // 5. SAVE
        repository.save(account);
    }

    public CurrentUserResponse getCurrentUser(
            UserAccount account
    ) {

        return new CurrentUserResponse(
                account.getUsername(),
                account.getRole().name()
        );
    }

    // ==============================
    // FORGOT PASSWORD
    // ==============================
    // 1. Xóa biến String html ở ngoài đi

    public void forgotPassword(ForgotPasswordRequest request) {
        UserAccount account = repository
                .findByStudent_Email(request.getEmail())
                .orElseThrow(() -> new AppException("Email not found", HttpStatus.NOT_FOUND));

        if (account.getStudent() == null) {
            throw new AppException("Student information not found", HttpStatus.BAD_REQUEST);
        }

        // Tạo mật khẩu mới
        String newPassword = generateRandomPassword();

        // Lưu vào DB
        account.setPassword(passwordEncoder.encode(newPassword));
        account.setRefreshToken(null);
        repository.save(account);

        // 2. Gọi hàm tạo HTML và truyền newPassword vào
        String finalHtml = buildResetPasswordEmail(newPassword);

        emailService.sendEmail(
                account.getStudent().getEmail(),
                "Dormitory Password Reset",
                finalHtml
        );
    }

    // 3. Tạo phương thức Helper để build HTML
    private String buildResetPasswordEmail(String password) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0; padding:0; background-color:#f4f7fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                    <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                        
                        <!-- Header -->
                        <div style="background-color: #2563eb; padding: 30px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px;">KTX Management System</h1>
                        </div>
                
                        <!-- Body -->
                        <div style="padding: 40px 30px; text-align: center;">
                            <h2 style="color: #1f2937; margin-top: 0;">Cấp lại mật khẩu mới 🔑</h2>
                            <p style="color: #4b5563; font-size: 16px; line-height: 1.6;">
                                Hệ thống đã nhận được yêu cầu khôi phục mật khẩu của bạn. <br>
                                <strong>Vui lòng sử dụng mật khẩu này để đăng nhập vào ứng dụng KTX.</strong>
                            </p>
                
                            <!-- Password Box -->
                            <div style="margin: 30px 0; background-color: #f0f7ff; border: 2px dashed #2563eb; border-radius: 8px; padding: 20px;">
                                <p style="margin: 0; color: #6b7280; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Mật khẩu của bạn là</p>
                                <h2 style="margin: 10px 0 0 0; color: #2563eb; font-size: 32px; letter-spacing: 4px;">
                                """ + password + """
                                </h2>
                            </div>
                
                            <!-- Warning -->
                            <div style="background-color: #fff7ed; border-left: 4px solid #f97316; padding: 15px; text-align: left; margin-top: 20px;">
                                <p style="margin: 0; color: #9a3412; font-size: 14px;">
                                    <strong>Lưu ý:</strong> Vì lý do bảo mật, bạn hãy thực hiện <b>đổi mật khẩu ngay</b> sau khi đăng nhập thành công vào ứng dụng.
                                </p>
                            </div>
                        </div>
                
                        <!-- Footer -->
                        <div style="background-color: #f9fafb; padding: 20px; text-align: center;">
                            <p style="margin: 0; color: #9ca3af; font-size: 12px;">
                                Đây là email tự động, vui lòng không phản hồi email này.<br>
                                &copy; 2024 Dormitory Management Team
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }

    // ==============================
    // RANDOM PASSWORD
    // ==============================

    private String generateRandomPassword() {

        String chars =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder password =
                new StringBuilder();

        for (int i = 0; i < 10; i++) {

            int index = (int)
                    (Math.random() * chars.length());

            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}