package com.stu.dormitory.modules.payment.repository;

import com.stu.dormitory.modules.payment.entity.Payment;
import com.stu.dormitory.modules.payment.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment>
    findByTransactionCode(
            String transactionCode
    );

    List<Payment>
    findByStatus(
            PaymentStatus status
    );
}