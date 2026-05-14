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
@Table(name = "students")
@Getter
@Setter
public class Student extends BaseEntity {

    @Column(unique = true)
    private String studentCode;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String cccd;

    private LocalDate dateOfBirth;

    private String gender;

    private String phone;

    @Column(unique = true)
    private String email;

    private String faculty;

    private String course;
}