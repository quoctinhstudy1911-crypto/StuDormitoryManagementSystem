package com.stu.dormitory.modules.application.enums;

/**
 * Trạng thái của đơn đăng ký KTX
 * Flow: PENDING → IN_REVIEW → VALID/INVALID → APPROVED/WAITING → CHECKED_IN/REJECTED → EXPIRED
 */
public enum ApplicationStatus {
    PENDING,      // Mới tạo, chờ admin xem xét
    IN_REVIEW,    // Admin đang xem xét hồ sơ
    VALID,        // Hồ sơ hợp lệ, chờ duyệt
    INVALID,      // Hồ sơ không hợp lệ, có thể gửi lại
    APPROVED,     // Được duyệt, có giường
    WAITING,      // Chờ giường (dự bị)
    CHECKED_IN,   // Đã nhập ký túc xá
    REJECTED,     // Bị từ chối (sau check-in)
    EXPIRED,      // Hết hạn (không check-in trong 3 ngày)
}
