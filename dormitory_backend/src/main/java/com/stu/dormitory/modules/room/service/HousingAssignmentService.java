package com.stu.dormitory.modules.room.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.application.entity.DormitoryApplication;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.application.repository.DormitoryApplicationRepository;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.payment.service.BillService;          // ✅ thêm import
import com.stu.dormitory.modules.room.dto.response.CurrentRoomResponse;
import com.stu.dormitory.modules.room.entity.*;
import com.stu.dormitory.modules.room.enums.AssignmentStatus;
import com.stu.dormitory.modules.room.enums.BedStatus;
import com.stu.dormitory.modules.room.enums.OccupancyPolicy;
import com.stu.dormitory.modules.room.enums.RoomStatus;
import com.stu.dormitory.modules.room.repository.BedRepository;
import com.stu.dormitory.modules.room.repository.RoomRepository;
import com.stu.dormitory.modules.room.repository.StudentHousingAssignmentRepository;

import com.stu.dormitory.modules.student.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;               // ✅ thêm import
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HousingAssignmentService {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final StudentHousingAssignmentRepository assignmentRepository;
    private final DormitoryApplicationRepository applicationRepository;
    private final BillService billService;

    /**
     * Reserve bed cho application
     */
    @Transactional
    public StudentHousingAssignment reserveBed(DormitoryApplication application) {
        OccupancyPolicy policy = (application.getGender() == Gender.MALE) ? OccupancyPolicy.MALE : OccupancyPolicy.FEMALE;

        List<Room> rooms = roomRepository.findAvailableRoomsByPolicy(policy, RoomStatus.AVAILABLE);
        if (rooms.isEmpty()) return null;

        for (Room room : rooms) {
            List<Bed> beds = bedRepository.findAvailableBeds(room.getId(), BedStatus.AVAILABLE);
            if (!beds.isEmpty()) {
                Bed bed = beds.get(0);
                bed.setStatus(BedStatus.RESERVED);
                bedRepository.save(bed);

                room.setOccupiedBeds(room.getOccupiedBeds() + 1);
                if (room.getOccupiedBeds() >= room.getCapacity()) {
                    room.setStatus(RoomStatus.FULL);
                }
                roomRepository.save(room);

                StudentHousingAssignment assignment = new StudentHousingAssignment();
                assignment.setApplication(application);
                assignment.setBed(bed);
                assignment.setStatus(AssignmentStatus.RESERVED);
                assignment.setReservedAt(LocalDateTime.now());

                return assignmentRepository.save(assignment);
            }
        }
        return null;
    }

    /**
     * Promote waiting list
     */
    @Transactional
    public void promoteWaitingList() {
        List<DormitoryApplication> waitingApplications = applicationRepository
                .findByStatusOrderByPriorityScoreDescSubmittedAtAsc(ApplicationStatus.WAITING_LIST);

        for (DormitoryApplication application : waitingApplications) {
            try {
                StudentHousingAssignment assignment = reserveBed(application);
                if (assignment != null) {
                    billService.createAccommodationBill(assignment, BigDecimal.valueOf(500000.0));

                    application.setStatus(ApplicationStatus.WAITING_PAYMENT);
                    application.setPaymentDeadline(LocalDateTime.now().plusDays(3));
                    applicationRepository.save(application);
                }
            } catch (Exception ex) {
                log.error("Failed to promote waiting application: {}", application.getApplicationCode(), ex);
            }
        }
    }
    public CurrentRoomResponse getCurrentRoom(UserAccount account) {
        Student student = account.getStudent();
        if (student == null) {
            throw new AppException("Student not found", HttpStatus.NOT_FOUND);
        }

        StudentHousingAssignment assignment = assignmentRepository
                .findByApplication_Student_IdAndStatus(student.getId(), AssignmentStatus.OCCUPIED)
                .orElseThrow(() -> new AppException("No room assigned", HttpStatus.NOT_FOUND));

        Bed bed = assignment.getBed();
        Room room = bed.getRoom();
        Floor floor = room.getFloor();
        Building building = floor.getBuilding();

        return CurrentRoomResponse.builder()
                .building(building.getCode())
                .floor(floor.getFloorNumber())
                .roomCode(room.getRoomCode())
                .bedCode(bed.getBedCode())
                .assignmentStatus(assignment.getStatus().name())
                .checkInAt(assignment.getCheckInAt())
                .build();
    }
}