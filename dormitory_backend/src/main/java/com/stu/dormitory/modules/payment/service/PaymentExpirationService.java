package com.stu.dormitory.modules.payment.service;

import com.stu.dormitory.modules.payment.entity.Bill;
import com.stu.dormitory.modules.payment.enums.BillStatus;
import com.stu.dormitory.modules.payment.repository.BillRepository;
import com.stu.dormitory.modules.room.entity.Bed;
import com.stu.dormitory.modules.room.entity.Room;
import com.stu.dormitory.modules.room.entity.StudentHousingAssignment;
import com.stu.dormitory.modules.room.enums.AssignmentStatus;
import com.stu.dormitory.modules.room.enums.BedStatus;
import com.stu.dormitory.modules.room.enums.RoomStatus;
import com.stu.dormitory.modules.room.repository.BedRepository;
import com.stu.dormitory.modules.room.repository.RoomRepository;
import com.stu.dormitory.modules.room.repository.StudentHousingAssignmentRepository;
import com.stu.dormitory.modules.room.service.HousingAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentExpirationService {

    private final BillRepository billRepository;
    private final StudentHousingAssignmentRepository assignmentRepository;
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final HousingAssignmentService housingAssignmentService;

    /**
     * Chạy vào đầu mỗi giờ (cron: "0 0 * * * *")
     * Xử lý các hóa đơn chưa thanh toán (UNPAID hoặc PARTIALLY_PAID) bị quá hạn.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireOverdueBills() {
        // Lấy cả UNPAID và PARTIALLY_PAID
        List<Bill> pendingBills = billRepository.findByStatusIn(
                List.of(BillStatus.UNPAID, BillStatus.PARTIALLY_PAID)
        );

        List<Bill> overdueBills = pendingBills.stream()
                .filter(this::isOverdue)
                .toList();

        if (overdueBills.isEmpty()) {
            log.debug("No overdue bills found");
            return;
        }

        log.info("Found {} overdue bills to process", overdueBills.size());

        boolean releasedAnyBed = false;

        for (Bill bill : overdueBills) {
            if (processOverdueBill(bill)) {
                releasedAnyBed = true;
            }
        }

        // Chỉ gọi promoteWaitingList một lần nếu có bất kỳ bed nào được giải phóng
        if (releasedAnyBed) {
            try {
                housingAssignmentService.promoteWaitingList();
                log.info("Waiting list processing triggered after releasing bed(s)");
            } catch (Exception e) {
                log.error("Failed to promote waiting list after releasing bed(s)", e);
            }
        }
    }

    private boolean isOverdue(Bill bill) {
        return bill.getDueDate() != null && bill.getDueDate().isBefore(LocalDate.now());
    }

    /**
     * Xử lý một bill quá hạn.
     * @return true nếu có bed được giải phóng (tức là có assignment RESERVED)
     */
    private boolean processOverdueBill(Bill bill) {
        bill.setStatus(BillStatus.OVERDUE);
        billRepository.save(bill);
        log.info("Marked bill {} as OVERDUE", bill.getId());

        StudentHousingAssignment assignment = bill.getAssignment();
        if (assignment != null && assignment.getStatus() == AssignmentStatus.RESERVED) {
            releaseAssignmentAndBed(assignment, bill.getId());
            return true;
        }
        return false;
    }

    private void releaseAssignmentAndBed(StudentHousingAssignment assignment, Long billId) {
        // Hủy assignment
        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignmentRepository.save(assignment);

        // Giải phóng giường
        Bed bed = assignment.getBed();
        bed.setStatus(BedStatus.AVAILABLE);
        bedRepository.save(bed);

        // Cập nhật phòng: tránh occupiedBeds bị âm
        Room room = bed.getRoom();
        int newOccupiedBeds = Math.max(room.getOccupiedBeds() - 1, 0);
        room.setOccupiedBeds(newOccupiedBeds);
        if (newOccupiedBeds < room.getCapacity()) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);

        log.info("Released resources: billId={}, assignmentId={}, bedId={}, roomId={}, newOccupiedBeds={}",
                billId, assignment.getId(), bed.getId(), room.getId(), newOccupiedBeds);
    }
}