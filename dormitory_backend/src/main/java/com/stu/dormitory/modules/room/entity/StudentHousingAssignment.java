package com.stu.dormitory.modules.room.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.application.entity.DormitoryApplication;
import com.stu.dormitory.modules.room.enums.AssignmentStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "student_housing_assignments")
public class StudentHousingAssignment extends BaseEntity {

    /**
     * Hồ sơ đã được duyệt
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "application_id",
            nullable = false
    )
    private DormitoryApplication application;

    /**
     * Giường được assign
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bed_id",
            nullable = false
    )
    private Bed bed;

    /**
     * RESERVED / OCCUPIED / COMPLETED...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    /**
     * Ngày reserve
     */
    private LocalDateTime reservedAt;

    /**
     * Ngày check-in thật
     */
    private LocalDateTime checkInAt;

    /**
     * Ngày checkout
     */
    private LocalDateTime checkOutAt;

}
