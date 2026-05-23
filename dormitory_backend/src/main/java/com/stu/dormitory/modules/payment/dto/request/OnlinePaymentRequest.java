package com.stu.dormitory.modules.payment.dto.request;

import com.stu.dormitory.modules.payment.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * REQUEST DTO:
 * Sinh viên thanh toán online.
 *
 * Flow:
 * USER
 *   ↓
 * Mobile App
 *   ↓
 * POST /api/payments/online
 */
@Getter
@Setter
public class OnlinePaymentRequest {

    /**
     * ID hóa đơn cần thanh toán
     */
    @NotNull(message = "Bill id is required")
    private Long billId;

    /**
     * Số tiền thanh toán
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Amount must be greater than 0"
    )
    private BigDecimal amount;

    /**
     * Phương thức thanh toán online
     *
     * Ví dụ:
     * - VNPAY
     * - MOMO
     * - BANK_TRANSFER
     */
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    /**
     * Mã giao dịch từ cổng thanh toán
     *
     * Có thể null khi test local.
     */
    private String transactionCode;
}

