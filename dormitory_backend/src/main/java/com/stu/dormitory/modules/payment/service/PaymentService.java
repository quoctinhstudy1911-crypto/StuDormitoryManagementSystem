package com.stu.dormitory.modules.payment.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.payment.dto.response.PaymentResponse;
import com.stu.dormitory.modules.payment.entity.Bill;
import com.stu.dormitory.modules.payment.entity.Payment;
import com.stu.dormitory.modules.payment.enums.BillStatus;
import com.stu.dormitory.modules.payment.enums.PaymentMethod;
import com.stu.dormitory.modules.payment.enums.PaymentStatus;
import com.stu.dormitory.modules.payment.repository.BillRepository;
import com.stu.dormitory.modules.payment.repository.PaymentRepository;
import com.stu.dormitory.modules.room.entity.Bed;
import com.stu.dormitory.modules.room.entity.StudentHousingAssignment;
import com.stu.dormitory.modules.room.enums.AssignmentStatus;
import com.stu.dormitory.modules.room.enums.BedStatus;
import com.stu.dormitory.modules.room.repository.BedRepository;
import com.stu.dormitory.modules.room.repository.StudentHousingAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final StudentHousingAssignmentRepository assignmentRepository;
    private final BedRepository bedRepository;

    // ==================== ONLINE PAYMENT (USER) ====================
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public PaymentResponse processOnlinePayment(Long billId,
                                                BigDecimal amount,
                                                PaymentMethod method,
                                                String transactionCode) {
        if (method == PaymentMethod.CASH) {
            throw new AppException("CASH payment is not allowed here", HttpStatus.BAD_REQUEST);
        }
        // Hiện tại giả lập success ngay. Khi tích hợp cổng thật, sẽ gọi sang bên thứ ba và trả về PENDING.
        return executePayment(billId, amount, method, transactionCode, PaymentStatus.SUCCESS);
    }

    // ==================== CASH PAYMENT (ADMIN) ====================
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PaymentResponse approveCashPayment(Long billId, BigDecimal amount) {
        String transactionCode = "CASH-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return executePayment(billId, amount, PaymentMethod.CASH, transactionCode, PaymentStatus.SUCCESS);
    }

    // ==================== PRIVATE COMMON LOGIC ====================
    private PaymentResponse executePayment(Long billId,
                                           BigDecimal amount,
                                           PaymentMethod method,
                                           String transactionCode,
                                           PaymentStatus paymentStatus) {
        // 1. Validate bill và amount
        Bill bill = validateBillAndAmount(billId, amount);

        // 2. Tạo payment record (luôn lưu, kể cả PENDING/FAILED)
        Payment payment = createPaymentRecord(bill, amount, method, transactionCode, paymentStatus);

        // 3. Chỉ khi payment thành công mới cập nhật bill và kích hoạt assignment
        if (paymentStatus == PaymentStatus.SUCCESS) {
            updateBillAfterPayment(bill, amount);
            if (bill.getStatus() == BillStatus.PAID) {
                activateAssignmentIfReserved(bill.getAssignment());
            }
        }

        // 4. Log đầy đủ
        log.info("Payment processed: billId={}, amount={}, method={}, transactionCode={}, paymentStatus={}, billStatus={}",
                bill.getId(), amount, method, payment.getTransactionCode(), payment.getStatus(),
                paymentStatus == PaymentStatus.SUCCESS ? bill.getStatus() : null);

        // 5. Build response
        return buildPaymentResponse(bill, payment);
    }

    private Bill validateBillAndAmount(Long billId, BigDecimal amount) {
        Bill bill = billRepository.findByIdForUpdate(billId)
                .orElseThrow(() -> new AppException("Bill not found", HttpStatus.NOT_FOUND));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new AppException("Bill already paid", HttpStatus.BAD_REQUEST);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("Invalid payment amount", HttpStatus.BAD_REQUEST);
        }
        BigDecimal remaining = bill.getAmount().subtract(bill.getPaidAmount());
        if (amount.compareTo(remaining) > 0) {
            throw new AppException("Payment exceeds remaining balance", HttpStatus.BAD_REQUEST);
        }
        return bill;
    }

    private Payment createPaymentRecord(Bill bill, BigDecimal amount, PaymentMethod method,
                                        String txnCode, PaymentStatus status) {
        if (txnCode == null || txnCode.isBlank()) {
            txnCode = "TXN-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        }
        if (paymentRepository.findByTransactionCode(txnCode).isPresent()) {
            log.warn("Duplicate transaction code: {}", txnCode);
            throw new AppException("Duplicate transaction", HttpStatus.BAD_REQUEST);
        }

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(status);
        payment.setTransactionCode(txnCode);
        // Chỉ set paidAt khi thanh toán thành công (SUCCESS)
        if (status == PaymentStatus.SUCCESS) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return paymentRepository.save(payment);
    }

    private void updateBillAfterPayment(Bill bill, BigDecimal amount) {
        BigDecimal newPaidAmount = bill.getPaidAmount().add(amount);
        bill.setPaidAmount(newPaidAmount);
        if (newPaidAmount.compareTo(bill.getAmount()) >= 0) {
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }
        billRepository.save(bill);
    }

    private void activateAssignmentIfReserved(StudentHousingAssignment assignment) {
        if (assignment != null && assignment.getStatus() == AssignmentStatus.RESERVED) {
            assignment.setStatus(AssignmentStatus.OCCUPIED);
            assignment.setCheckInAt(LocalDateTime.now());
            assignmentRepository.save(assignment);

            Bed bed = assignment.getBed();
            bed.setStatus(BedStatus.OCCUPIED);
            bedRepository.save(bed);

            log.info("Activated assignment and bed: assignmentId={}, bedId={}",
                    assignment.getId(), bed.getId());
        }
    }

    private PaymentResponse buildPaymentResponse(Bill bill, Payment payment) {
        StudentHousingAssignment assignment = bill.getAssignment();
        boolean isSuccess = payment.getStatus() == PaymentStatus.SUCCESS;

        return PaymentResponse.builder()
                // PAYMENT
                .paymentId(payment.getId())
                .paymentStatus(payment.getStatus())
                .paymentMethod(payment.getMethod())
                .transactionCode(payment.getTransactionCode())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                // BILL
                .billId(bill.getId())
                .billStatus(isSuccess ? bill.getStatus() : null)
                .paidAmount(isSuccess ? bill.getPaidAmount() : null)
                // ASSIGNMENT
                .assignmentStatus(isSuccess && assignment != null ? assignment.getStatus() : null)
                // MESSAGE
                .message(isSuccess ? "Payment successful" : "Payment failed")
                .build();
    }
}