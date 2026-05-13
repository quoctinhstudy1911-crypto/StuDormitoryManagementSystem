package com.stu.dormitory.modules.eligible.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * EligibleStudent Entity - Danh sách sinh viên đủ điều kiện
 * 
 * Tác dụng:
 * - Lưu danh sách sinh viên được phép đăng ký cho mỗi kỳ tuyển sinh
 * - Kiểm tra điều kiện khi sinh viên đăng ký (registration)
 * - Có thể kích hoạt/vô hiệu hóa (isActive)
 * 
 * Unique: Một sinh viên (CCCD) chỉ có 1 record trên mỗi period
 */
@Entity
@Table(name = "eligible_student",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cccd", "periodId"}))
@Getter @Setter
public class EligibleStudent extends BaseEntity {

    private String cccd;           // Căn cước công dân
    private String fullName;       // Họ tên
    private String studentCode;    // Mã sinh viên
    private Boolean isActive;      // Có đủ điều kiện không?
    private Long periodId;         // Kỳ tuyển sinh
}