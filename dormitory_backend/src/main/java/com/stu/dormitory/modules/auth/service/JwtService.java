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

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    // ==============================
    // GENERATE ACCESS TOKEN
    // ==============================

    public String generateAccessToken(UserAccount account) {

        return Jwts.builder()

                .setSubject(account.getUsername())

                .claim("role", account.getRole().name())

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtConfig.getAccessExpiration()
                        )
                )

                .signWith(
                        getAccessSigningKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    // ==============================
    // GENERATE REFRESH TOKEN
    // ==============================

    public String generateRefreshToken(UserAccount account) {

        return Jwts.builder()

                .setSubject(account.getUsername())

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtConfig.getRefreshExpiration()
                        )
                )

                .signWith(
                        getRefreshSigningKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    // ==============================
    // EXTRACT USERNAME
    // ==============================

    public String extractUsernameFromAccessToken(String token) {

        return extractAccessClaims(token)
                .getSubject();
    }

    // ==============================
    // VALIDATE ACCESS TOKEN
    // ==============================

    public void validateAccessToken(String token) {

        try {

            extractAccessClaims(token);

        } catch (JwtException e) {

            throw new AppException(
                    "Invalid or expired access token",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    // ==============================
    // VALIDATE REFRESH TOKEN
    // ==============================

    public void validateRefreshToken(String token) {

        try {

            extractRefreshClaims(token);

        } catch (JwtException e) {

            throw new AppException(
                    "Invalid or expired refresh token",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    // ==============================
    // EXTRACT ACCESS CLAIMS
    // ==============================

    private Claims extractAccessClaims(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(getAccessSigningKey())

                .build()

                .parseClaimsJws(token)

                .getBody();
    }

    // ==============================
    // EXTRACT REFRESH CLAIMS
    // ==============================

    private Claims extractRefreshClaims(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(getRefreshSigningKey())

                .build()

                .parseClaimsJws(token)

                .getBody();
    }

    // ==============================
    // ACCESS SIGNING KEY
    // ==============================

    private Key getAccessSigningKey() {

        return Keys.hmacShaKeyFor(
                jwtConfig.getAccessSecret().getBytes()
        );
    }

    // ==============================
    // REFRESH SIGNING KEY
    // ==============================

    private Key getRefreshSigningKey() {

        return Keys.hmacShaKeyFor(
                jwtConfig.getRefreshSecret().getBytes()
        );
    }
    public String extractUsernameFromRefreshToken(
            String token
    ) {

        return extractRefreshClaims(token)
                .getSubject();
    }
}