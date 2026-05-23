package com.stu.dormitory.modules.payment.controller;

import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.payment.dto.request.CashPaymentRequest;
import com.stu.dormitory.modules.payment.dto.request.OnlinePaymentRequest;
import com.stu.dormitory.modules.payment.dto.response.PaymentResponse;
import com.stu.dormitory.modules.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * PAYMENT CONTROLLER
 * -------------------------------------------------------------------
 * Chịu trách nhiệm:
 *
 * - nhận request payment
 * - validate request DTO
 * - gọi PaymentService
 * - trả JSON response
 *
 * FLOW:
 *
 * Client
 *   ↓
 * Controller
 *   ↓
 * Service
 *   ↓
 * Database
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * USER thanh toán online.
     *
     * Ví dụ:
     * - VNPay
     * - MoMo
     * - Bank Transfer
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/online")
    public ApiResponse<PaymentResponse>
    processOnlinePayment(

            @Valid
            @RequestBody
            OnlinePaymentRequest request
    ) {

        PaymentResponse response =
                paymentService.processOnlinePayment(
                        request.getBillId(),
                        request.getAmount(),
                        request.getPaymentMethod(),
                        request.getTransactionCode()
                );

        return new ApiResponse<>(
                true,
                "Online payment successful",
                response
        );
    }

    /**
     * ADMIN xác nhận thanh toán tiền mặt.
     *
     * Flow:
     * Student pays cash
     *   ↓
     * Admin confirms
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cash/approve")
    public ApiResponse<PaymentResponse>
    approveCashPayment(

            @Valid
            @RequestBody
            CashPaymentRequest request
    ) {

        PaymentResponse response =
                paymentService.approveCashPayment(
                        request.getBillId(),
                        request.getAmount()
                );

        return new ApiResponse<>(
                true,
                "Cash payment approved successfully",
                response
        );
    }
}