package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.application.enums.RegistrationType;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "dormitory_applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_application_cccd_period",
                        columnNames = {"cccd", "registration_period_id"}
                )
        }
)
@Getter
@Setter
public class DormitoryApplication extends BaseEntity {

    /**
     * OPTIMISTIC LOCKING: Ngăn chặn xung đột khi 2 Admin cùng duyệt 1 hồ sơ.
     * Nếu Admin A đang sửa mà Admin B lưu trước, hệ thống sẽ báo lỗi cho Admin A.
     */
    @Version
    private Long version;

    /**
     * RANKING FIELD: Dùng để sắp xếp thứ tự xét duyệt (Điểm ưu tiên).
     */
    @Column(name = "priority_score")
    private Integer priorityScore = 0;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

    @Column(name = "revision_deadline")
    private LocalDateTime revisionDeadline;

    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * CHUYỂN SANG LAZY: Chỉ tải thông tin Admin khi cần thiết (ví dụ: xem chi tiết hồ sơ).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private UserAccount reviewedBy;

    @Column(name = "application_code", nullable = false, unique = true)
    private String applicationCode;

    @Column(nullable = false)
    private String cccd;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(name = "student_code")
    private String studentCode;

    /**
     * LAZY LOADING: Tránh việc Join bảng RegistrationPeriod vô tội vạ khi lấy danh sách đơn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_period_id", nullable = false)
    private RegistrationPeriod registrationPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type", nullable = false)
    private RegistrationType registrationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "application_pdf_url")
    private String applicationPdfUrl;
}