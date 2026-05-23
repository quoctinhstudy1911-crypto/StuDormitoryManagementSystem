package com.stu.dormitory.modules.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * REQUEST DTO:
 * Admin xác nhận thanh toán tiền mặt.
 *
 * Flow:
 * Student pays cash
 *   ↓
 * Admin confirms
 *   ↓
 * POST /api/payments/cash/approve
 */
@Getter
@Setter
public class CashPaymentRequest {

    /**
     * ID hóa đơn
     */
    @NotNull(message = "Bill id is required")
    private Long billId;

    /**
     * Số tiền nhận được
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Amount must be greater than 0"
    )
    private BigDecimal amount;
}

