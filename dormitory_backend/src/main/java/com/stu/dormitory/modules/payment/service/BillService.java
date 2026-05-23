package com.stu.dormitory.modules.payment.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.payment.entity.Bill;
import com.stu.dormitory.modules.payment.enums.BillStatus;
import com.stu.dormitory.modules.payment.enums.BillType;
import com.stu.dormitory.modules.payment.repository.BillRepository;
import com.stu.dormitory.modules.room.entity.StudentHousingAssignment;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;

    /**
     * Tạo bill tiền ở KTX
     *
     * Flow:
     * Assignment approved
     *    ↓
     * Create accommodation bill
     *    ↓
     * Student payment
     *    ↓
     * Check-in
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Bill createAccommodationBill(
            StudentHousingAssignment assignment,
            BigDecimal amount
    ) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("Invalid bill amount", HttpStatus.BAD_REQUEST);
        }

        Bill bill = new Bill();
        bill.setAssignment(assignment);
        bill.setBillType(BillType.ACCOMMODATION_FEE);
        bill.setAmount(amount);
        bill.setPaidAmount(BigDecimal.ZERO);
        bill.setStatus(BillStatus.UNPAID);
        bill.setDueDate(LocalDate.now().plusDays(3));
        bill.setDescription("Accommodation fee");
        return billRepository.save(bill);
    }
}