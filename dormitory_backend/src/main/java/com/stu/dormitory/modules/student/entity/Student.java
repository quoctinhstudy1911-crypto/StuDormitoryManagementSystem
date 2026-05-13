package com.stu.dormitory.modules.student.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Student Entity - Thông tin sinh viên
 *
 * CCCD là unique identifier chính
 * Được tạo tự động khi sinh viên đăng ký ứng dụng
 */
@Entity
@Table(name = "student")
@Getter @Setter
public class Student extends BaseEntity {

    private String studentCode;  // Mã sinh viên
    private String fullName;     // Họ tên đầy đủ

    @Column(unique = true, nullable = false)
    private String cccd;         // Căn cước công dân (ID duy nhất)

    private LocalDate dateOfBirth;  // Ngày sinh
    private String gender;          // Giới tính
    private String phone;           // Số điện thoại
    private String email;           // Email

}