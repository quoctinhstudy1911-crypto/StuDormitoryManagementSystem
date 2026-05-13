package com.stu.dormitory.modules.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApplicationResponseDTO {
    private Long id;
    private String status;
    private Long periodId;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime checkinAt;
    private String studentCccd;
    private Long studentId;
    private Double score;
    private LocalDateTime validatedAt;
    private String validationReason;
    private String rejectionReason;
    private String bedId;
    private String roomId;

    // Constructor for backward compatibility (original 9 parameters)
    public ApplicationResponseDTO(Long id, String status, Long periodId, LocalDateTime submittedAt,
                                 LocalDateTime verifiedAt, LocalDateTime approvedAt, LocalDateTime checkinAt,
                                 String studentCccd, Long studentId) {
        this.id = id;
        this.status = status;
        this.periodId = periodId;
        this.submittedAt = submittedAt;
        this.verifiedAt = verifiedAt;
        this.approvedAt = approvedAt;
        this.checkinAt = checkinAt;
        this.studentCccd = studentCccd;
        this.studentId = studentId;
    }
}

