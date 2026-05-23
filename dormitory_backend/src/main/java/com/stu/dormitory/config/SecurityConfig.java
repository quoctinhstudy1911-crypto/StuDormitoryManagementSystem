package com.stu.dormitory.config;

import com.stu.dormitory.modules.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SECURITY CONFIGURATION
 * -------------------------------------------------------------------
 * Chịu trách nhiệm:
 *
 * 1. Cấu hình xác thực (Authentication)
 * 2. Cấu hình phân quyền (Authorization)
 * 3. Cấu hình JWT Filter
 * 4. Cấu hình Security Exception
 * 5. Cấu hình Stateless API
 *
 * Kiến thức:
 * - Spring Security
 * - JWT Authentication
 * - Filter Chain
 * - Method Security
 */
@Configuration
@RequiredArgsConstructor

/**
 * Cho phép sử dụng:
 * @PreAuthorize(...)
 * @PostAuthorize(...)
 */
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * JWT FILTER
     * ---------------------------------------------------------
     * Filter tự custom để:
     *
     * - Đọc JWT token
     * - Validate token
     * - Lấy UserAccount
     * - Gắn Authentication vào SecurityContext
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * PASSWORD ENCODER
     * ---------------------------------------------------------
     * Dùng BCrypt để hash password.
     *
     * Không bao giờ lưu password plain text.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /**
     * AUTHENTICATION MANAGER
     * ---------------------------------------------------------
     * Spring Security component dùng cho:
     *
     * - authenticate login
     * - xác thực username/password
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * SECURITY FILTER CHAIN
     * ---------------------------------------------------------
     * Trái tim của Spring Security.
     *
     * Request sẽ đi qua:
     *
     * Client
     *   ↓
     * SecurityFilterChain
     *   ↓
     * JwtAuthenticationFilter
     *   ↓
     * Authorization
     *   ↓
     * Controller
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

                /**
                 * ENABLE CORS
                 * -------------------------------------------------
                 * Cho phép frontend gọi API từ domain khác.
                 *
                 * Ví dụ:
                 * - localhost:3000
                 * - mobile app
                 */
                .cors(cors -> {})

                /**
                 * DISABLE CSRF
                 * -------------------------------------------------
                 * Vì hệ thống dùng JWT Stateless API.
                 *
                 * CSRF thường dùng cho:
                 * - Session
                 * - Cookie authentication
                 */
                .csrf(AbstractHttpConfigurer::disable)

                /**
                 * STATELESS SESSION
                 * -------------------------------------------------
                 * Không lưu session trên server.
                 *
                 * Mỗi request:
                 * - tự mang JWT token
                 * - server validate lại
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /**
                 * SECURITY EXCEPTION HANDLING
                 * -------------------------------------------------
                 * Xử lý lỗi security.
                 *
                 * 401:
                 * - chưa đăng nhập
                 * - token sai
                 * - token hết hạn
                 *
                 * 403:
                 * - đã đăng nhập
                 * - nhưng không có quyền
                 */
                .exceptionHandling(exceptions -> exceptions

                        /**
                         * AUTHENTICATION ENTRY POINT
                         * -------------------------------------------------
                         * Bắt lỗi:
                         * - không token
                         * - token invalid
                         * - token expired
                         *
                         * Flow:
                         * Request
                         *   ↓
                         * SecurityFilterChain
                         *   ↓
                         * Authentication fail
                         *   ↓
                         * authenticationEntryPoint
                         *   ↓
                         * 401
                         */
                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.setCharacterEncoding(
                                            "UTF-8"
                                    );

                                    response.setStatus(
                                            HttpStatus.UNAUTHORIZED.value()
                                    );

                                    response.getWriter().write("""
                                            {
                                                "success": false,
                                                "message": "Unauthorized"
                                            }
                                            """);

                                    response.getWriter().flush();
                                }
                        )

                        /**
                         * ACCESS DENIED HANDLER
                         * -------------------------------------------------
                         * Chỉ bắt:
                         * URL-level authorization.
                         *
                         * Ví dụ:
                         * .requestMatchers("/api/admin/**")
                         * .hasRole("ADMIN")
                         *
                         * KHÔNG bắt:
                         * @PreAuthorize
                         *
                         * Vì @PreAuthorize chạy ở:
                         * Method Security Layer
                         */
                        .accessDeniedHandler(
                                (request, response, exception) -> {

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.setCharacterEncoding(
                                            "UTF-8"
                                    );

                                    response.setStatus(
                                            HttpStatus.FORBIDDEN.value()
                                    );

                                    response.getWriter().write("""
                                            {
                                                "success": false,
                                                "message": "Access denied"
                                            }
                                            """);

                                    response.getWriter().flush();
                                }
                        )
                )

                /**
                 * AUTHORIZATION RULES
                 * -------------------------------------------------
                 * Cấu hình API nào:
                 *
                 * - public
                 * - cần login
                 * - cần role
                 */
                .authorizeHttpRequests(auth -> auth

                        /**
                         * PUBLIC AUTH APIs
                         * -------------------------------------------------
                         * Không cần token.
                         */
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refresh-token",
                                "/api/auth/forgot-password"
                        ).permitAll()

                        /**
                         * PUBLIC APPLICATION APIs
                         * -------------------------------------------------
                         * Sinh viên chưa login vẫn có thể:
                         * - kiểm tra eligibility
                         * - đăng ký hồ sơ
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications/check-eligibility"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications"
                        ).permitAll()

                        /**
                         * PUBLIC GET APIs
                         * -------------------------------------------------
                         * Cho phép xem:
                         * - chi tiết hồ sơ
                         * - danh sách hồ sơ
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/applications/upload-document").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/applications/priority-documents").permitAll()

                        /**
                         * ADMIN APIs
                         * -------------------------------------------------
                         * Chỉ ADMIN mới được truy cập.
                         */
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        /**
                         * USER APIs
                         * -------------------------------------------------
                         * USER hoặc ADMIN đều truy cập được.
                         */
                        .requestMatchers(
                                "/api/users/**"
                        ).hasAnyRole("USER", "ADMIN")

                        /**
                         * TẤT CẢ API KHÁC
                         * -------------------------------------------------
                         * Bắt buộc phải login.
                         */
                        .anyRequest().authenticated()
                )

                /**
                 * ADD JWT FILTER
                 * -------------------------------------------------
                 * Chèn custom JWT filter vào trước:
                 *
                 * UsernamePasswordAuthenticationFilter
                 *
                 * để:
                 * - validate JWT
                 * - set SecurityContext
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

