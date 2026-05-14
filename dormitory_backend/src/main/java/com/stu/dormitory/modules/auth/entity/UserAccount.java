package com.stu.dormitory.modules.auth.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.auth.enums.Role;
import com.stu.dormitory.modules.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts")
@Getter
@Setter
public class UserAccount extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime lastLogin;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;
}