package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Application Entity - Đơn đăng ký KTX
 *
 * Cấu trúc:
 * - Lưu trữ đơn đăng ký từ sinh viên
 * - Tracking tất cả trạng thái: PENDING → IN_REVIEW → VALID/INVALID → APPROVED/WAITING → CHECKED_IN
 * - Lưu lý do khi từ chối (INVALID hoặc REJECTED)
 * - Lưu giường/phòng khi duyệt
 * - Lưu deadline 3 ngày cho check-in
 *
 * Unique Constraint: Một sinh viên chỉ có 1 đơn trên 1 kỳ tuyển sinh
 */
@Entity
@Table(name = "application",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "period_id"}))
@Getter
@Setter
public class Application extends BaseEntity {

    // Relationship
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Registration Period Reference
    @Column(name = "period_id")
    private Long periodId;

    // Application State & Details
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private Double score;
    private String note;
    private Boolean isPriorityManual = false;

    // Timestamps for workflow tracking
    private LocalDateTime submittedAt;      // Khi sinh viên submit
    private LocalDateTime verifiedAt;       // Khi tất cả docs xác minh
    private LocalDateTime validatedAt;      // Khi admin validate (VALID/INVALID)
    private LocalDateTime approvedAt;       // Khi admin approve
    private LocalDateTime checkinAt;        // Khi sinh viên check-in
    private LocalDateTime paymentDeadline;  // 3 ngày deadline (nếu APPROVED)

    // Rejection/Validation Info
    private String validationReason;  // Lý do nếu INVALID
    private String rejectionReason;   // Lý do nếu REJECTED

    // Bed Assignment
    private String bedId;   // Mã giường được cấp
    private String roomId;  // Mã phòng được cấp
}


