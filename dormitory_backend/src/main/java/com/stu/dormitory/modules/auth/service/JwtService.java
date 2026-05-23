package com.stu.dormitory.modules.auth.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.config.JwtConfig;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Dịch vụ xử lý JWT (JSON Web Token):
 * - Tạo access token và refresh token
 * - Trích xuất username từ token
 * - Xác thực tính hợp lệ của token
 *
 * @author Dormitory Team
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    // ==============================
    // TẠO ACCESS TOKEN (ngắn hạn)
    // ==============================

    public String generateAccessToken(UserAccount account) {
        return Jwts.builder()
                .setSubject(account.getUsername())
                .claim("role", account.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessExpiration()))
                .signWith(getAccessSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==============================
    // TẠO REFRESH TOKEN (dài hạn)
    // ==============================

    public String generateRefreshToken(UserAccount account) {
        return Jwts.builder()
                .setSubject(account.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshExpiration()))
                .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==============================
    // TRÍCH XUẤT USERNAME
    // ==============================

    public String extractUsernameFromAccessToken(String token) {
        return extractAccessClaims(token).getSubject();
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractRefreshClaims(token).getSubject();
    }

    // ==============================
    // XÁC THỰC TOKEN
    // ==============================

    public void validateAccessToken(String token) {
        try {
            extractAccessClaims(token);
        } catch (JwtException e) {
            throw new AppException("Invalid or expired access token", HttpStatus.UNAUTHORIZED);
        }
    }

    public void validateRefreshToken(String token) {
        try {
            extractRefreshClaims(token);
        } catch (JwtException e) {
            throw new AppException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }
    }

    // ==============================
    // LẤY CLAIMS
    // ==============================

    private Claims extractAccessClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims extractRefreshClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getRefreshSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==============================
    // CHUẨN BỊ KHÓA KÝ
    // ==============================

    private SecretKey getAccessSigningKey() {
        String secret = jwtConfig.getAccessSecret();
        validateSecretKey(secret, "Access");
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getRefreshSigningKey() {
        String secret = jwtConfig.getRefreshSecret();
        validateSecretKey(secret, "Refresh");
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Kiểm tra secret key có hợp lệ không:
     * - Không null, không rỗng
     * - Độ dài tối thiểu 32 bytes (256 bit) theo yêu cầu của HS256
     * Nếu không đủ, ném exception ngay khi khởi tạo bean (giúp phát hiện sớm)
     */
    private void validateSecretKey(String secret, String keyType) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException(keyType + " secret key must not be null or empty");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    keyType + " secret key must be at least 32 bytes long (current: " + keyBytes.length + ")"
            );
        }
    }
}