package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.application.enums.VerificationDocumentType;
import com.stu.dormitory.modules.application.enums.VerificationStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_documents")
@Getter
@Setter
public class VerificationDocument
        extends BaseEntity {

    // Hồ sơ đăng ký
    @ManyToOne
    @JoinColumn(nullable = false)
    private DormitoryApplication application;

    // Loại giấy tờ
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationDocumentType documentType;

    // Link file cloudinary/s3
    @Column(nullable = false)
    private String fileUrl;

    // Trạng thái verify
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;

    // Ghi chú verify
    @Column(columnDefinition = "TEXT")
    private String note;

    // Thời gian verify
    private LocalDateTime verifiedAt;
}