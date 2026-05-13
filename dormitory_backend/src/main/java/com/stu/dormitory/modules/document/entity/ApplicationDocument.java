package com.stu.dormitory.modules.document.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.application.entity.Application;
import com.stu.dormitory.modules.document.enums.DocumentStatus;
import com.stu.dormitory.modules.document.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ApplicationDocument Entity - Tài liệu/hồ sơ của ứng dụng
 *
 * Loại tài liệu (DocumentType):
 * - CCCD: Căn cước công dân
 * - PRIORITY: Giấy ưu tiên
 * - COMMITMENT: Giấy cam kết nội trú
 *
 * Trạng thái (DocumentStatus):
 * - PENDING: Vừa upload, chờ xác minh
 * - VALID: Hợp lệ
 * - INVALID: Không hợp lệ
 */
@Entity
@Getter @Setter
public class ApplicationDocument extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;  // Liên kết tới ứng dụng

    @Enumerated(EnumType.STRING)
    private DocumentType type;  // Loại tài liệu

    private String fileUrl;     // URL của file

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;  // Trạng thái xác minh
}