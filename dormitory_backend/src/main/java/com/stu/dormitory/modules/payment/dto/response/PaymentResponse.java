package com.stu.dormitory.modules.payment.dto.response;

import com.stu.dormitory.modules.payment.enums.BillStatus;
import com.stu.dormitory.modules.payment.enums.PaymentMethod;
import com.stu.dormitory.modules.payment.enums.PaymentStatus;
import com.stu.dormitory.modules.room.enums.AssignmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RESPONSE DTO:
 * Kết quả thanh toán.
 */
@Getter
@Builder
public class PaymentResponse {

    /**
     * Payment ID
     */
    private Long paymentId;

    /**
     * Bill ID
     */
    private Long billId;

    /**
     * Mã giao dịch
     */
    private String transactionCode;

    /**
     * Số tiền thanh toán
     */
    private BigDecimal amount;

    /**
     * Payment method
     */
    private PaymentMethod paymentMethod;

    /**
     * Payment status
     */
    private PaymentStatus paymentStatus;

    /**
     * Bill status sau thanh toán
     */
    private BillStatus billStatus;

    /**
     * Assignment status sau thanh toán
     */
    private AssignmentStatus assignmentStatus;

    /**
     * Tổng số tiền thanh toán
     */
    private BigDecimal paidAmount;

    /**
     * Thời gian thanh toán
     */
    private LocalDateTime paidAt;

    /**
     * Message
     */
    private String message;
}
