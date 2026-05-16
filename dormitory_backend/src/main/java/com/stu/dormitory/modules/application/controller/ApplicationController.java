package com.stu.dormitory.modules.application.controller;

import com.stu.dormitory.common.response.ApiResponse;

import com.stu.dormitory.modules.application.dto.request.CheckEligibilityRequest;
import com.stu.dormitory.modules.application.dto.request.CreateApplicationRequest;
import com.stu.dormitory.modules.application.dto.request.UploadDocumentRequest;

import com.stu.dormitory.modules.application.dto.request.UploadPriorityDocumentRequest;
import com.stu.dormitory.modules.application.dto.response.ApplicationResponse;
import com.stu.dormitory.modules.application.dto.response.CheckEligibilityResponse;
import com.stu.dormitory.modules.application.dto.response.DocumentResponse;

import com.stu.dormitory.modules.application.service.ApplicationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // ==============================
    // CHECK ELIGIBILITY
    // ==============================

    @PostMapping("/check-eligibility")
    public ApiResponse<CheckEligibilityResponse>
    checkEligibility(

            @Valid
            @RequestBody
            CheckEligibilityRequest request
    ) {

        CheckEligibilityResponse response =
                applicationService
                        .checkEligibility(request);

        return new ApiResponse<>(
                true,
                "Eligibility checked successfully",
                response
        );
    }

    // ==============================
    // CREATE APPLICATION
    // ==============================

    @PostMapping
    public ApiResponse<ApplicationResponse>
    createApplication(

            @Valid
            @RequestBody
            CreateApplicationRequest request
    ) {

        ApplicationResponse response =
                applicationService
                        .createApplication(request);

        return new ApiResponse<>(
                true,
                "Application created successfully",
                response
        );
    }

    // ==============================
    // UPLOAD DOCUMENT
    // ==============================

    @PostMapping("/upload-document")
    public ApiResponse<DocumentResponse>
    uploadDocument(

            @Valid
            @RequestBody
            UploadDocumentRequest request
    ) {

        DocumentResponse response =
                applicationService
                        .uploadDocument(request);

        return new ApiResponse<>(
                true,
                "Document uploaded successfully",
                response
        );
    }

    @PostMapping("/priority-documents")
    public ApiResponse<?> uploadPriorityDocument(
            @Valid
            @RequestBody
            UploadPriorityDocumentRequest request
    ) {

        applicationService
                .uploadPriorityDocument(request);

        return new ApiResponse<>(
                true,
                "Upload priority document successfully",
                null
        );
    }
}