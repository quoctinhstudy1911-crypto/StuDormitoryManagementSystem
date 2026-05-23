package com.stu.dormitory.common.exception;

import com.stu.dormitory.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GLOBAL EXCEPTION HANDLER
 * -------------------------------------------------------------------
 * Chịu trách nhiệm:
 *
 * - bắt exception toàn hệ thống
 * - trả JSON response thống nhất
 *
 * FLOW:
 *
 * Controller
 *    ↓
 * Service
 *    ↓
 * throw Exception
 *    ↓
 * GlobalExceptionHandler
 *    ↓
 * JSON Response
 *
 * Kiến thức:
 * - Exception Handling
 * - REST API Error Response
 * - Spring Boot Advice
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BUSINESS EXCEPTION
     * ---------------------------------------------------------
     * Bắt:
     * - AppException custom
     *
     * Ví dụ:
     * - not found
     * - duplicate
     * - invalid business
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>>
    handleAppException(AppException ex) {

        log.warn(
                "Business exception: {}",
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(
                        new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null
                        )
                );
    }

    /**
     * METHOD SECURITY EXCEPTION
     * ---------------------------------------------------------
     * Bắt lỗi:
     *
     * @PreAuthorize(...)
     *
     * Ví dụ:
     * USER gọi API ADMIN
     *
     * Vì:
     * @PreAuthorize chạy ở:
     * Method Security Layer
     *
     * nên:
     * KHÔNG đi qua:
     * accessDeniedHandler()
     */
    @ExceptionHandler(
            AuthorizationDeniedException.class
    )
    public ResponseEntity<ApiResponse<?>>
    handleAuthorizationDeniedException(
            AuthorizationDeniedException ex
    ) {

        log.warn(
                "Access denied: {}",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        new ApiResponse<>(
                                false,
                                "Access denied",
                                null
                        )
                );
    }

    /**
     * SYSTEM EXCEPTION
     * ---------------------------------------------------------
     * Bắt tất cả exception chưa xử lý.
     *
     * Ví dụ:
     * - NullPointerException
     * - SQL Exception
     * - RuntimeException
     *
     * Không expose lỗi hệ thống thật ra frontend.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>>
    handleException(Exception ex) {

        log.error(
                "Unexpected system error",
                ex
        );

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        new ApiResponse<>(
                                false,
                                "Internal server error",
                                null
                        )
                );
    }
}
