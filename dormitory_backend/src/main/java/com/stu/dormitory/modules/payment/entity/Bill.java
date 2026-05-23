package com.stu.dormitory.modules.payment.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.payment.enums.BillStatus;
import com.stu.dormitory.modules.payment.enums.BillType;
import com.stu.dormitory.modules.room.entity.StudentHousingAssignment;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "bills")
public class Bill extends BaseEntity {
    /**
     * ACCOMMODATION / ELECTRIC / WATER...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillType billType;

    /**
     * Tổng tiền cần thanh toán
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Số tiền đã thanh toán
     */
    @Column(nullable = false)
    private BigDecimal paidAmount =
            BigDecimal.ZERO;

    /**
     * Trạng thái bill
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status =
            BillStatus.UNPAID;

    /**
     * Hạn thanh toán
     */
    private LocalDate dueDate;

    /**
     * Nội dung bill
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Assignment liên quan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private StudentHousingAssignment assignment;

}
