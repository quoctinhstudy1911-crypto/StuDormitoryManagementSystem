package com.stu.dormitory.modules.application.controller;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.application.dto.ApplicationRequestDTO;
import com.stu.dormitory.modules.application.dto.ApplicationResponseDTO;
import com.stu.dormitory.modules.application.entity.Application;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ApplicationController - REST endpoints cho Application
 *
 * User Endpoints:
 * - POST /applications - Đăng ký mới
 * - GET /applications/status - Kiểm tra trạng thái
 * - PUT /applications/{id}/resubmit - Gửi lại (INVALID)
 * - PUT /applications/{id}/checkin - Check-in
 *
 * Admin Endpoints:
 * - PUT /applications/{id}/review - Bắt đầu review
 * - PUT /applications/{id}/validate - Validate (VALID/INVALID)
 * - PUT /applications/{id}/approve - Approve (APPROVED/WAITING)
 * - PUT /applications/{id}/assign-bed - Cấp giường
 * - PUT /applications/{id}/promote-from-waiting - Nâng từ WAITING
 * - PUT /applications/{id}/reject - Reject sau check-in
 * - GET /applications/admin/by-status - Lọc theo trạng thái
 * - GET /applications/admin/by-period/{id} - Lọc theo period
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // =============================
    // 1. CREATE APPLICATION
    // =============================
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@RequestBody @Valid ApplicationRequestDTO req) {

        Application app = applicationService.createApplication(
                req.getCccd(),
                req.getPeriodId()
        );

        ApplicationResponseDTO res = new ApplicationResponseDTO(
                app.getId(),
                app.getStatus().name(),
                app.getPeriodId(),
                app.getSubmittedAt(),
                app.getVerifiedAt(),
                app.getApprovedAt(),
                app.getCheckinAt(),
                app.getStudent().getCccd(),
                app.getStudent().getId()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký thành công", res));
    }

    // =============================
    // 2. REVIEW APPLICATION (Chuyển sang IN_REVIEW)
    // =============================
    @PutMapping("/{id}/review")
    public ResponseEntity<ApiResponse<?>> review(@PathVariable Long id) {

        applicationService.reviewApplication(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Chuyển sang xem xét thành công", null));
    }

    // =============================
    // 3. VALIDATE APPLICATION (VALID hoặc INVALID)
    // =============================
    @PutMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<?>> validate(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String reason
    ) {

        applicationService.validateApplication(id, status, reason);

        return ResponseEntity.ok(new ApiResponse<>(true, "Xác nhận hồ sơ thành công", null));
    }

    // =============================
    // 4. VERIFY DOCUMENTS
    // =============================
    @PutMapping("/{id}/verify-documents")
    public ResponseEntity<ApiResponse<?>> verifyDocuments(@PathVariable Long id) {

        applicationService.verifyDocuments(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Xác minh hồ sơ thành công", null));
    }

    // =============================
    // 5. RESUBMIT APPLICATION (Gửi lại nếu INVALID)
    // =============================
    @PutMapping("/{id}/resubmit")
    public ResponseEntity<ApiResponse<?>> resubmit(@PathVariable Long id) {

        applicationService.resubmitApplication(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Gửi lại hồ sơ thành công", null));
    }

    // =============================
    // 6. GET NEXT IN QUEUE
    // =============================
    @GetMapping("/next")
    public ResponseEntity<ApiResponse<?>> getNext() {

        Application app = applicationService.getNext();

        ApplicationResponseDTO res = new ApplicationResponseDTO(
                app.getId(),
                app.getStatus().name(),
                app.getPeriodId(),
                app.getSubmittedAt(),
                app.getVerifiedAt(),
                app.getApprovedAt(),
                app.getCheckinAt(),
                app.getStudent().getCccd(),
                app.getStudent().getId()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Success", res));
    }

    // =============================
    // 7. APPROVE APPLICATION
    // =============================
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approve(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status
    ) {

        applicationService.approve(id, status);

        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thành công", null));
    }

    // =============================
    // 8. ASSIGN BED
    // =============================
    @PutMapping("/{id}/assign-bed")
    public ResponseEntity<ApiResponse<?>> assignBed(
            @PathVariable Long id,
            @RequestParam String bedId,
            @RequestParam(required = false) String roomId
    ) {

        applicationService.assignBed(id, bedId, roomId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Cấp giường thành công", null));
    }

    // =============================
    // 9. PROMOTE FROM WAITING
    // =============================
    @PutMapping("/{id}/promote-from-waiting")
    public ResponseEntity<ApiResponse<?>> promoteFromWaiting(@PathVariable Long id) {

        applicationService.promoteFromWaiting(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Nâng cấp từ danh sách chờ thành công", null));
    }

    // =============================
    // 10. CHECK-IN
    // =============================
    @PutMapping("/{id}/checkin")
    public ResponseEntity<ApiResponse<?>> checkin(@PathVariable Long id) {

        applicationService.confirmCheckin(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Check-in thành công", null));
    }

    // =============================
    // 11. REJECT AFTER CHECK-IN
    // =============================
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<?>> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {

        applicationService.rejectAfterCheckin(id, reason);

        return ResponseEntity.ok(new ApiResponse<>(true, "Từ chối thành công", null));
    }

    // =============================
    // 12. MARK AS EXPIRED
    // =============================
    @PutMapping("/{id}/mark-expired")
    public ResponseEntity<ApiResponse<?>> markExpired(@PathVariable Long id) {

        applicationService.markAsExpired(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Đánh dấu hết hạn thành công", null));
    }

    // =============================
    // 13. GET APPLICATION STATUS
    // =============================
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getApplicationStatus(
            @RequestParam String cccd,
            @RequestParam Long periodId) {

        Application app = applicationService.getApplicationByCccdAndPeriod(cccd, periodId)
                .orElseThrow(() -> new AppException("Không tìm thấy đơn đăng ký"));

        ApplicationResponseDTO res = new ApplicationResponseDTO(
                app.getId(),
                app.getStatus().name(),
                app.getPeriodId(),
                app.getSubmittedAt(),
                app.getVerifiedAt(),
                app.getApprovedAt(),
                app.getCheckinAt(),
                app.getStudent().getCccd(),
                app.getStudent().getId()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", res));
    }

    // =============================
    // 14. GET APPLICATION BY ID
    // =============================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getApplication(@PathVariable Long id) {

        Application app = applicationService.getApplicationById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy đơn đăng ký"));

        ApplicationResponseDTO res = new ApplicationResponseDTO(
                app.getId(),
                app.getStatus().name(),
                app.getPeriodId(),
                app.getSubmittedAt(),
                app.getVerifiedAt(),
                app.getApprovedAt(),
                app.getCheckinAt(),
                app.getStudent().getCccd(),
                app.getStudent().getId()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", res));
    }

    // =============================
    // 15. GET BY STATUS (for admin)
    // =============================
    @GetMapping("/admin/by-status")
    public ResponseEntity<ApiResponse<?>> getByStatus(@RequestParam ApplicationStatus status) {

        List<Application> apps = applicationService.getByStatus(status);

        return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", apps));
    }

    // =============================
    // 16. GET BY PERIOD (for admin)
    // =============================
    @GetMapping("/admin/by-period/{periodId}")
    public ResponseEntity<ApiResponse<?>> getByPeriod(@PathVariable Long periodId) {

        List<Application> apps = applicationService.getByPeriod(periodId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", apps));
    }
}

