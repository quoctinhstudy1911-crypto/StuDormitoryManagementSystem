package com.stu.dormitory.modules.payment.dto.response;

import com.stu.dormitory.modules.payment.enums.BillStatus;
import com.stu.dormitory.modules.payment.enums.BillType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * RESPONSE DTO:
 * Thông tin hóa đơn cho mobile app/frontend.
 */
@Getter
@Builder
public class BillResponse {

    /**
     * Bill ID
     */
    private Long billId;

    /**
     * Loại hóa đơn
     */
    private BillType billType;

    /**
     * Tổng tiền hóa đơn
     */
    private BigDecimal amount;

    /**
     * Số tiền đã thanh toán
     */
    private BigDecimal paidAmount;

    /**
     * Số tiền còn thiếu
     */
    private BigDecimal remainingAmount;

    /**
     * Trạng thái hóa đơn
     */
    private BillStatus status;

    /**
     * Hạn thanh toán
     */
    private LocalDate dueDate;

    /**
     * Mô tả hóa đơn
     */
    private String description;

    /**
     * Assignment ID liên quan
     */
    private Long assignmentId;

    /**
     * Mã phòng
     */
    private String roomCode;

    /**
     * Mã giường
     */
    private String bedCode;
}
