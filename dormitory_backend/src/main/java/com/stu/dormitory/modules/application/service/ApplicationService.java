package com.stu.dormitory.modules.application.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.application.dto.request.*;
import com.stu.dormitory.modules.application.dto.response.ApplicationResponse;
import com.stu.dormitory.modules.application.dto.response.CheckEligibilityResponse;
import com.stu.dormitory.modules.application.dto.response.DocumentResponse;
import com.stu.dormitory.modules.application.entity.*;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.PriorityCategory;
import com.stu.dormitory.modules.application.enums.VerificationStatus;
import com.stu.dormitory.modules.application.repository.*;
import com.stu.dormitory.modules.upload.service.CloudinaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final RegistrationEligibilityRepository eligibilityRepository;
    private final DormitoryApplicationRepository applicationRepository;
    private final VerificationDocumentRepository documentRepository;
    private final ApplicationPriorityRepository applicationPriorityRepository;
    private final PriorityDocumentRepository priorityDocumentRepository;

    private final PdfService pdfService;
    private final CloudinaryService cloudinaryService;

    private RegistrationPeriod getActivePeriod() {
        LocalDateTime now = LocalDateTime.now();
        return registrationPeriodRepository
                .findByActiveTrueAndStartDateBeforeAndEndDateAfter(now, now)
                .orElseThrow(() -> new AppException(
                        "Hiện không trong thời gian đăng ký nội trú",
                        HttpStatus.NOT_FOUND
                ));
    }

    public CheckEligibilityResponse checkEligibility(CheckEligibilityRequest request) {
        RegistrationPeriod period = getActivePeriod();
        boolean eligible = eligibilityRepository
                .existsByCccdAndRegistrationPeriod_Id(request.getCccd(), period.getId());
        return CheckEligibilityResponse.builder()
                .eligible(eligible)
                .registrationPeriodName(period.getName())
                .target(period.getTarget().name())
                .build();
    }

    @Transactional
    public ApplicationResponse createApplication(CreateApplicationRequest request) {
        RegistrationPeriod period = getActivePeriod();
        validateApplication(request, period);

        DormitoryApplication application = new DormitoryApplication();
        application.setApplicationCode(generateApplicationCode());
        application.setCccd(request.getCccd());
        application.setFullName(request.getFullName());
        application.setGender(request.getGender());
        application.setEmail(request.getEmail());
        application.setPhone(request.getPhone());
        application.setStudentCode(request.getStudentCode());
        application.setNote(request.getNote());
        application.setRegistrationType(request.getRegistrationType());
        application.setStatus(ApplicationStatus.PENDING);
        application.setSubmittedAt(LocalDateTime.now());
        application.setRegistrationPeriod(period);

        application = applicationRepository.save(application);

        int maxScore = 0;
        if (request.getPriorityCategories() != null && !request.getPriorityCategories().isEmpty()) {
            for (PriorityCategory category : request.getPriorityCategories()) {
                ApplicationPriority priority = new ApplicationPriority();
                priority.setApplication(application);
                priority.setCategory(category);
                applicationPriorityRepository.save(priority);
                if (category.getScore() > maxScore) {
                    maxScore = category.getScore();
                }
            }
        }
        application.setPriorityScore(maxScore);
        application = applicationRepository.save(application);

        File pdfFile = null;
        try {
            pdfFile = pdfService.generateApplicationPdf(application);
            String pdfUrl = cloudinaryService.uploadFile(pdfFile, "dormitory_pdfs");
            application.setApplicationPdfUrl(pdfUrl);
            application = applicationRepository.save(application);
        } catch (Exception e) {
            log.error("Create application failed: {}", e.getMessage());
            throw new AppException("Cannot complete application process", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (pdfFile != null && pdfFile.exists()) {
                pdfFile.delete();
            }
        }
        return mapToResponse(application);
    }

    @Transactional
    public DocumentResponse uploadDocument(UploadDocumentRequest request) {
        DormitoryApplication application = applicationRepository
                .findByApplicationCode(request.getApplicationCode())
                .orElseThrow(() -> new AppException("Không tìm thấy hồ sơ đăng ký", HttpStatus.NOT_FOUND));

        // Kiểm tra trạng thái hồ sơ: chỉ được upload khi còn xử lý (PENDING, UNDER_REVIEW, REVISION_REQUIRED)
        ApplicationStatus status = application.getStatus();
        if (status != ApplicationStatus.PENDING
                && status != ApplicationStatus.UNDER_REVIEW
                && status != ApplicationStatus.REVISION_REQUIRED) {
            throw new AppException("Cannot upload document at current application status: " + status, HttpStatus.BAD_REQUEST);
        }

        VerificationDocument document = new VerificationDocument();
        document.setApplication(application);
        document.setDocumentType(request.getDocumentType());
        document.setFileUrl(request.getFileUrl());
        document.setVerificationStatus(VerificationStatus.PENDING);
        documentRepository.save(document);

        return DocumentResponse.builder()
                .id(document.getId())
                .documentType(document.getDocumentType())
                .fileUrl(document.getFileUrl())
                .verificationStatus(document.getVerificationStatus())
                .build();
    }

    @Transactional
    public void uploadPriorityDocument(UploadPriorityDocumentRequest request) {
        DormitoryApplication application = applicationRepository
                .findByApplicationCode(request.getApplicationCode())
                .orElseThrow(() -> new AppException("Application not found", HttpStatus.NOT_FOUND));

        // Chỉ được upload giấy tờ ưu tiên khi hồ sơ còn xử lý
        ApplicationStatus status = application.getStatus();
        if (status != ApplicationStatus.PENDING
                && status != ApplicationStatus.UNDER_REVIEW
                && status != ApplicationStatus.REVISION_REQUIRED) {
            throw new AppException("Cannot upload priority document at current application status: " + status, HttpStatus.BAD_REQUEST);
        }

        PriorityDocument document = new PriorityDocument();
        document.setApplication(application);
        document.setDocumentType(request.getDocumentType());
        document.setFileUrl(request.getFileUrl());
        document.setVerificationStatus(VerificationStatus.PENDING);
        priorityDocumentRepository.save(document);
    }

    // --- HELPERS ---
    private void validateApplication(CreateApplicationRequest request, RegistrationPeriod period) {
        boolean eligible = eligibilityRepository
                .existsByCccdAndRegistrationPeriod_Id(request.getCccd(), period.getId());
        if (!eligible) {
            throw new AppException("Bạn không thuộc diện được đăng ký trong đợt này", HttpStatus.BAD_REQUEST);
        }
        if (applicationRepository.existsByCccdAndRegistrationPeriod_Id(request.getCccd(), period.getId())) {
            throw new AppException("Bạn đã nộp hồ sơ cho đợt đăng ký này rồi", HttpStatus.BAD_REQUEST);
        }
    }

    private String generateApplicationCode() {
        String code;
        do {
            code = "APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (applicationRepository.existsByApplicationCode(code));
        return code;
    }

    private ApplicationResponse mapToResponse(DormitoryApplication application) {
        return ApplicationResponse.builder()
                .applicationCode(application.getApplicationCode())
                .fullName(application.getFullName())
                .cccd(application.getCccd())
                .email(application.getEmail())
                .phone(application.getPhone())
                .studentCode(application.getStudentCode())
                .registrationType(application.getRegistrationType())
                .status(application.getStatus())
                .applicationPdfUrl(application.getApplicationPdfUrl())
                .build();
    }
}