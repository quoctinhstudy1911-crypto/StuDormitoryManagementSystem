package com.stu.dormitory.modules.auth.filter;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;
import com.stu.dormitory.modules.auth.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserAccountRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // ==========================================
        // NO TOKEN
        // ==========================================
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // ==========================================
            // VALIDATE TOKEN
            // ==========================================
            jwtService.validateAccessToken(token);

            String username = jwtService.extractUsernameFromAccessToken(token);

            // ==========================================
            // LOAD USER
            // ==========================================
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserAccount account = repository.findByUsername(username).orElse(null);

                // Không tìm thấy account
                if (account == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // ==========================================
                // SOFT DELETE CHECK
                // ==========================================
                if (Boolean.TRUE.equals(account.getIsDeleted())) {
                    log.warn("Deleted account access attempt: {}", username);
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Account has been deleted");
                    return;
                }

                // ==========================================
                // ACTIVE CHECK
                // ==========================================
                if (!Boolean.TRUE.equals(account.getIsActive())) {
                    log.warn("Inactive account access attempt: {}", username);
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Account is inactive");
                    return;
                }

                // ==========================================
                // CREATE AUTHENTICATION
                // ==========================================
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                account,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (AppException ex) {
            // ==========================================
            // JWT ERROR
            // ==========================================
            log.warn("JWT authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());

        } catch (Exception ex) {
            // ==========================================
            // UNEXPECTED ERROR
            // ==========================================
            log.error("Unexpected authentication error", ex);
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication processing failed");
        }
    }

    /**
     * Send JSON error response
     */
    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("status", status);
        body.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}