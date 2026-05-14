package com.stu.dormitory.modules.auth.filter;

import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;
import com.stu.dormitory.modules.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserAccountRepository repository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ==============================
        // GET AUTH HEADER
        // ==============================

        final String authHeader =
                request.getHeader("Authorization");

        // NO TOKEN
        if (
                authHeader == null
                        ||
                        !authHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(request, response);

            return;
        }

        // ==============================
        // EXTRACT TOKEN
        // ==============================

        final String token =
                authHeader.substring(7);

        // VALIDATE TOKEN
        jwtService.validateAccessToken(token);

        // EXTRACT USERNAME
        String username =
                jwtService.extractUsernameFromAccessToken(token);

        // ==============================
        // CHECK SECURITY CONTEXT
        // ==============================

        if (
                username != null
                        &&
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication() == null
        ) {

            // FIND ACCOUNT
            UserAccount account = repository
                    .findByUsername(username)
                    .orElse(null);

            // ACCOUNT NOT FOUND
            if (account == null) {

                filterChain.doFilter(request, response);

                return;
            }

            // CHECK SOFT DELETE
            if (account.getIsDeleted()) {

                filterChain.doFilter(request, response);

                return;
            }

            // CHECK ACTIVE
            if (!account.getIsActive()) {

                filterChain.doFilter(request, response);

                return;
            }

            // ==============================
            // CREATE AUTHENTICATION
            // ==============================

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            account,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + account.getRole().name()
                                    )
                            )
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // SAVE TO SECURITY CONTEXT
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);
        }

        // CONTINUE FILTER CHAIN
        filterChain.doFilter(request, response);
    }
}