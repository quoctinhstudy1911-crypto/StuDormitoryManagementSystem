package com.stu.dormitory.common.exception;

import com.stu.dormitory.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Này là flow của bắt lỗi
 * Controller
 *    ↓
 * Service
 *    ↓
 * throw Exception
 *    ↓
 * GlobalExceptionHandler bắt
 *    ↓
 * Trả JSON lỗi về frontend
 */

@RestControllerAdvice // Annotation bắt exception toàn bộ project nha
public class GlobalExceptionHandler {

    /**
     * Quy tắc chung nè
     throw new AppException("Room is full")
     ↓
     Spring bắt được
     ↓
     đưa exception vào biến ex
     */

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException ex) {
      return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiResponse<>(
                      false,
                      ex.getMessage(),
                      null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {

        // INTERNAL_SERVER_ERROR: 500
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        false,
                        "Lỗi hệ thống",
                        null
                ));
    }
}
/**
 * Kiến thức ResponseEntity đại diện cho HTTP Response
 * Chứa status Header và body (json)
 */