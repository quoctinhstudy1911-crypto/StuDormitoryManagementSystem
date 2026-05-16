package com.stu.dormitory.modules.application.enums;

public enum ApplicationStatus {

    PENDING, // Mới submit

    UNDER_REVIEW, // Admin đang xử lí

    REVISION_REQUIRED, // Thiếu giấy tờ

    WAITING_PAYMENT, // Đậu nhưng chưa đóng tiền

    APPROVED, // Hoàn tất

    REJECTED, // Từ chối

    WAITING_LIST, // Hết slot

    EXPIRED // Quá hạn
}