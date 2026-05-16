package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.application.enums.RegistrationTarget;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_periods")
@Getter
@Setter
public class RegistrationPeriod
        extends BaseEntity {

    // Tên đợt đăng ký
    @Column(nullable = false)
    private String name;

    // Đối tượng được đăng ký
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationTarget target;

    // Thời gian mở đăng ký
    @Column(nullable = false)
    private LocalDateTime startDate;

    // Thời gian đóng đăng ký
    @Column(nullable = false)
    private LocalDateTime endDate;

    // Số lượng tối đa
    @Column(nullable = false)
    private Integer quota;

    // Đợt còn hoạt động không
    @Column(nullable = false)
    private Boolean active = true;
}