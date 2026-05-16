package com.stu.dormitory.modules.application.controller;

import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.application.dto.request.AdminReviewRequest;
import com.stu.dormitory.modules.application.dto.request.ReviewPriorityDocumentRequest;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationDetailResponse;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationResponse;
import com.stu.dormitory.modules.application.dto.response.PriorityDocumentResponse;
import com.stu.dormitory.modules.application.entity.DormitoryApplication;
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

    @GetMapping("/pending")
    public ApiResponse<List<AdminApplicationResponse>> getPendingApplications()
    {
        List<AdminApplicationResponse> listDormitoryApplications = applicationReviewService.getPendingApplications();

        return new ApiResponse<>(
                true,
                "Get pending applications successfully",
                listDormitoryApplications
        );
    }

    @GetMapping("/{applicationCode}")
    public ApiResponse<AdminApplicationDetailResponse> getApplicationDetail(
            @PathVariable String applicationCode // Bắt lấy phần biến đổi trong code dùng để mà lấy đối tượng cụ thể
            // Khác với tham số truy vấn @RequestParam
    ) {
        AdminApplicationDetailResponse response =
                applicationReviewService.getApplicationDetail(applicationCode);

        return new ApiResponse<>(
                true,
                "Get application detail successfully",
                response
        );
    }

    @PostMapping("/{applicationCode}/review")
    public ApiResponse<?> reviewApplication(
            @PathVariable String applicationCode,
            // @RequestBody chuyển json thành đối tượng AdminReviewRequest và dùng @Valid để kiểm tra xem có hợp lệ
            // Với quy tắc đặt trong DTO
            @Valid @RequestBody AdminReviewRequest request
    ) {
        applicationReviewService.reviewApplication(applicationCode, request);

        return new ApiResponse<>(
                true,
                "Review application successfully",
                null
        );
    }

    @PostMapping("/priority-documents/{documentId}/review")
    public ApiResponse<?> reviewPriorityDocument(
            @PathVariable Long documentId,

            @Valid @RequestBody
            ReviewPriorityDocumentRequest request
    )
    {
        applicationReviewService.verifyPriorityDocument(documentId,request);
        return new ApiResponse<>(
                true,
                "Review priority document successfully",
                null
        );
    }

    @GetMapping("/{applicationCode}/priority-documents")
    public ApiResponse<List<PriorityDocumentResponse>> getPriorityDocuments(
            @PathVariable String applicationCode
    ) {
        List<PriorityDocumentResponse> response =
                applicationReviewService.getPriorityDocuments(applicationCode);

        return new ApiResponse<>(
                true,
                "Get priority documents successfully",
                response
        );
    }

}
