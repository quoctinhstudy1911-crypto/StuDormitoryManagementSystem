package com.stu.dormitory.modules.application.controller;

import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.application.dto.request.AdminReviewRequest;
import com.stu.dormitory.modules.application.dto.request.VerifyDocumentRequest;
import com.stu.dormitory.modules.application.dto.request.VerifyPriorityDocumentRequest;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationDetailResponse;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationResponse;
import com.stu.dormitory.modules.application.dto.response.PriorityDocumentResponse;
import com.stu.dormitory.modules.application.service.ApplicationReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/applications")
public class ApplicationReviewController {

    private final ApplicationReviewService applicationReviewService;

    /**
     * GET PENDING APPLICATIONS
     */
    @GetMapping("/pending")
    public ApiResponse<List<AdminApplicationResponse>> getPendingApplications() {
        List<AdminApplicationResponse> applications = applicationReviewService.getPendingApplications();
        return new ApiResponse<>(true, "Get pending applications successfully", applications);
    }

    /**
     * GET APPLICATION DETAIL
     */
    @GetMapping("/{applicationCode}")
    public ApiResponse<AdminApplicationDetailResponse> getApplicationDetail(@PathVariable String applicationCode) {
        AdminApplicationDetailResponse response = applicationReviewService.getApplicationDetail(applicationCode);
        return new ApiResponse<>(true, "Get application detail successfully", response);
    }

    /**
     * REVIEW APPLICATION (APPROVE / REJECT / REQUEST_SUPPLEMENT)
     */
    @PostMapping("/{applicationCode}/review")
    public ApiResponse<?> reviewApplication(@PathVariable String applicationCode,
                                            @Valid @RequestBody AdminReviewRequest request) {
        applicationReviewService.reviewApplication(applicationCode, request);
        return new ApiResponse<>(true, "Review application successfully", null);
    }

    // ==================== NORMAL DOCUMENT VERIFICATION ====================
    /**
     * VERIFY NORMAL DOCUMENT (CCCD, PORTRAIT_PHOTO, COMMITMENT)
     */
    @PostMapping("/documents/{documentId}/verify")
    public ApiResponse<?> verifyNormalDocument(@PathVariable Long documentId,
                                               @Valid @RequestBody VerifyDocumentRequest request) {
        applicationReviewService.verifyNormalDocument(documentId, request);
        return new ApiResponse<>(true, "Document verified successfully", null);
    }

    // ==================== PRIORITY DOCUMENTS ====================
    /**
     * GET PRIORITY DOCUMENTS
     */
    @GetMapping("/{applicationCode}/priority-documents")
    public ApiResponse<List<PriorityDocumentResponse>> getPriorityDocuments(@PathVariable String applicationCode) {
        List<PriorityDocumentResponse> response = applicationReviewService.getPriorityDocuments(applicationCode);
        return new ApiResponse<>(true, "Get priority documents successfully", response);
    }

    /**
     * VERIFY PRIORITY DOCUMENT
     */
    @PostMapping("/priority-documents/{documentId}/verify")
    public ApiResponse<?> verifyPriorityDocument(@PathVariable Long documentId,
                                                 @Valid @RequestBody VerifyPriorityDocumentRequest request) {
        applicationReviewService.verifyPriorityDocument(documentId, request);
        return new ApiResponse<>(true, "Verify priority document successfully", null);
    }
}