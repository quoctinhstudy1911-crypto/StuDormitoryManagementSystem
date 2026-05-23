package com.stu.dormitory.modules.auth.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.common.service.EmailService;
import com.stu.dormitory.modules.auth.dto.request.ForgotPasswordRequest;
import com.stu.dormitory.modules.auth.dto.request.LoginRequest;
import com.stu.dormitory.modules.auth.dto.response.LoginResponse;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Service xử lý các nghiệp vụ xác thực và phân quyền:
 * - Đăng nhập, đăng xuất, refresh token
 * - Đổi mật khẩu, quên mật khẩu
 * - Lấy thông tin người dùng hiện tại
 *
 * @author Dormitory Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    // SecureRandom thay thế Math.random() – đảm bảo tính ngẫu nhiên mật khẩu (bảo mật hơn)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // Bộ ký tự dùng để tạo mật khẩu tạm thời (có cả ký tự đặc biệt)
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

    /**
     * ĐĂNG NHẬP HỆ THỐNG
     * - Kiểm tra tài khoản tồn tại, bị xoá mềm, bị vô hiệu hoá
     * - Xác thực mật khẩu
     * - Tạo access token & refresh token
     * - Lưu refresh token và thời gian đăng nhập cuối
     * @param request chứa username (CCCD) và password
     * @return LoginResponse chứa token và role
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        UserAccount account = repository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new AppException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        // Kiểm tra soft delete (tài khoản bị xoá logic)
        if (Boolean.TRUE.equals(account.getIsDeleted())) {
            throw new AppException("Account has been deleted", HttpStatus.FORBIDDEN);
        }
        // Kiểm tra trạng thái active (khoá/mở)
        if (!Boolean.TRUE.equals(account.getIsActive())) {
            throw new AppException("Account is inactive", HttpStatus.FORBIDDEN);
        }
        // So khớp mật khẩu đã mã hoá
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new AppException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        // Sinh token
        String accessToken = jwtService.generateAccessToken(account);
        String refreshToken = jwtService.generateRefreshToken(account);

        // Lưu refresh token (dùng để cấp lại access token khi hết hạn)
        account.setRefreshToken(refreshToken);
        account.setLastLogin(LocalDateTime.now());
        repository.save(account);

        return new LoginResponse(accessToken, refreshToken, account.getRole().name());
    }

    /**
     * Đăng xuất người dùng hiện tại.
     * - Xoá refresh token của tài khoản đang đăng nhập khỏi cơ sở dữ liệu.
     * - Sau khi logout, refresh token cũ không thể dùng để cấp mới access token.
     * - Client bắt buộc phải đăng nhập lại để nhận refresh token mới.

     * Lưu ý: Access token vẫn còn giá trị cho đến khi hết hạn,
     * nhưng vì không có refresh token hợp lệ, người dùng sẽ không thể duy trì phiên lâu dài.
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount account = (UserAccount) authentication.getPrincipal();
        account.setRefreshToken(null);
        repository.save(account);
    }

    /**
     * LÀM MỚI ACCESS TOKEN (REFRESH TOKEN)
     * - Kiểm tra refresh token còn hiệu lực
     * - So khớp với refresh token đã lưu trong DB (chống lấy cắp token)
     * - Cấp mới access token và refresh token (xoay vòng refresh)
     * @param refreshToken token dạng JWT
     * @return LoginResponse chứa cặp token mới
     */
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        jwtService.validateRefreshToken(refreshToken);
        String username = jwtService.extractUsernameFromRefreshToken(refreshToken);
        UserAccount account = repository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException("Account not found", HttpStatus.NOT_FOUND));

        // Phòng chống việc refresh token bị đánh cắp: token trong DB phải khớp với token gửi lên
        if (account.getRefreshToken() == null || !account.getRefreshToken().equals(refreshToken)) {
            throw new AppException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtService.generateAccessToken(account);
        String newRefreshToken = jwtService.generateRefreshToken(account);
        account.setRefreshToken(newRefreshToken);
        repository.save(account);

        return new LoginResponse(newAccessToken, newRefreshToken, account.getRole().name());
    }

    /**
     * ĐỔI MẬT KHẨU CHO NGƯỜI DÙNG HIỆN TẠI
     * Quy trình xử lý:
     * 1. Lấy thông tin tài khoản từ SecurityContext (đảm bảo đã đăng nhập)
     * 2. Kiểm tra mật khẩu cũ có khớp với mật khẩu trong DB không
     * 3. Mã hoá mật khẩu mới bằng BCrypt
     * 4. Xoá refresh token để buộc người dùng đăng nhập lại bằng mật khẩu mới
     * 5. Lưu thay đổi vào database

     * Lưu ý: Phương thức yêu cầu người dùng phải được xác thực (isAuthenticated)

     * @param oldPassword mật khẩu cũ (dạng plain text từ request)
     * @param newPassword mật khẩu mới (dạng plain text từ request)
     * @throws AppException nếu mật khẩu cũ không đúng (HTTP 400) hoặc không có quyền (HTTP 401)
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        // 1. Lấy authentication từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra authentication hợp lệ (phòng trường hợp bị null hoặc principal sai kiểu)
        if (authentication == null || !(authentication.getPrincipal() instanceof UserAccount)) {
            throw new AppException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        UserAccount account = (UserAccount) authentication.getPrincipal();

        // 3. Xác thực mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new AppException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        // 4. Mã hoá và cập nhật mật khẩu mới
        account.setPassword(passwordEncoder.encode(newPassword));

        // 5. Xoá refresh token để vô hiệu hoá phiên đăng nhập cũ
        account.setRefreshToken(null);

        // 6. Lưu thay đổi
        repository.save(account);
    }

    /**
     * QUÊN MẬT KHẨU – GỬI MẬT KHẨU MỚI QUA EMAIL
     * - Không báo lỗi cụ thể "Email not found" vì lý do bảo mật (tránh brute force email)
     * - Nếu email tồn tại, tạo mật khẩu ngẫu nhiên bằng SecureRandom (bảo mật hơn Math.random)
     * - Gửi mật khẩu qua email, yêu cầu người dùng đổi ngay sau đăng nhập
     * @param request chứa email sinh viên
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Tìm tài khoản qua email của student (mối quan hệ @OneToOne)
        // KHÔNG throw exception khi không tìm thấy -> tránh lộ thông tin email có tồn tại hay không
        UserAccount account = repository
                .findByStudent_Email(request.getEmail())
                .orElse(null);

        if (account == null || account.getStudent() == null) {
            log.info("Password reset requested for non-existent email: {}", request.getEmail());
            // Trả về bình thường, không báo lỗi (security)
            return;
        }

        // Tạo mật khẩu ngẫu nhiên an toàn (12 ký tự, bao gồm ký tự đặc biệt)
        String newPassword = generateSecureRandomPassword();
        account.setPassword(passwordEncoder.encode(newPassword));
        account.setRefreshToken(null);  // Vô hiệu hoá refresh token cũ sau khi đặt lại mật khẩu
        repository.save(account);

        // Gửi email với mật khẩu mới
        String finalHtml = buildResetPasswordEmail(newPassword);
        emailService.sendEmail(
                account.getStudent().getEmail(),
                "Dormitory Password Reset",
                finalHtml
        );
        log.info("Password reset email sent to {}", account.getStudent().getEmail());
    }

    /**
     * XÂY DỰNG NỘI DUNG EMAIL RESET PASSWORD (HTML)
     * @param password mật khẩu mới (plain text)
     * @return chuỗi HTML có style đẹp, thông báo lưu ý bảo mật
     */
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
                        <div style="background-color: #2563eb; padding: 30px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px;">KTX Management System</h1>
                        </div>
                        <div style="padding: 40px 30px; text-align: center;">
                            <h2 style="color: #1f2937; margin-top: 0;">Cấp lại mật khẩu mới 🔑</h2>
                            <p style="color: #4b5563; font-size: 16px; line-height: 1.6;">
                                Hệ thống đã nhận được yêu cầu khôi phục mật khẩu của bạn. <br>
                                <strong>Vui lòng sử dụng mật khẩu này để đăng nhập vào ứng dụng KTX.</strong>
                            </p>
                            <div style="margin: 30px 0; background-color: #f0f7ff; border: 2px dashed #2563eb; border-radius: 8px; padding: 20px;">
                                <p style="margin: 0; color: #6b7280; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Mật khẩu của bạn là</p>
                                <h2 style="margin: 10px 0 0 0; color: #2563eb; font-size: 32px; letter-spacing: 4px;">
                                """ + password + """
                                </h2>
                            </div>
                            <div style="background-color: #fff7ed; border-left: 4px solid #f97316; padding: 15px; text-align: left; margin-top: 20px;">
                                <p style="margin: 0; color: #9a3412; font-size: 14px;">
                                    <strong>Lưu ý:</strong> Vì lý do bảo mật, bạn hãy thực hiện <b>đổi mật khẩu ngay</b> sau khi đăng nhập thành công vào ứng dụng.
                                </p>
                            </div>
                        </div>
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

    /**
     * TẠO MẬT KHẨU NGẪU NHIÊN AN TOÀN
     * Sử dụng SecureRandom thay vì Math.random để đảm bảo tính ngẫu nhiên mật mã học.
     * @return chuỗi mật khẩu gồm 12 ký tự (chữ hoa, thường, số, ký tự đặc biệt)
     */
    private String generateSecureRandomPassword() {
        StringBuilder password = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            password.append(PASSWORD_CHARS.charAt(SECURE_RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }
}