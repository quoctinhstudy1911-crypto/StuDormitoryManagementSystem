package com.stu.dormitory.modules.payment.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.payment.enums.PaymentMethod;
import com.stu.dormitory.modules.payment.enums.PaymentStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    /**
     * Bill được thanh toán
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bill_id",
            nullable = false
    )
    private Bill bill;

    /**
     * Số tiền thanh toán
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * CASH / MOMO / VNPAY...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    /**
     * PENDING / SUCCESS / FAILED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status =
            PaymentStatus.PENDING;

    /**
     * Mã giao dịch duy nhất
     */
    @Column(
            unique = true,
            nullable = false
    )
    private String transactionCode;

    /**
     * Nội dung giao dịch
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Thời gian thanh toán thành công
     */
    private LocalDateTime paidAt;

}
