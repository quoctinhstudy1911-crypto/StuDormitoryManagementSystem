package com.stu.dormitory.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ApiResponse - Standard response format cho tất cả API
 *
 * Cấu trúc:
 * {
 *   "success": true/false,
 *   "message": "Mô tả",
 *   "data": {...}  // T là generic type
 * }
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;  // Thành công hay thất bại
    private String message;   // Thông báo cho user
    private T data;          // Dữ liệu trả về (có thể null)
}