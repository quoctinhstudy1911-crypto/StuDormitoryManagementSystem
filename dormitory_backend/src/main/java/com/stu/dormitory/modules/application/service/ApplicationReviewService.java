package com.stu.dormitory.modules.application.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.application.dto.request.AdminReviewRequest;
import com.stu.dormitory.modules.application.dto.request.VerifyDocumentRequest;
import com.stu.dormitory.modules.application.dto.request.VerifyPriorityDocumentRequest;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationDetailResponse;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationResponse;
import com.stu.dormitory.modules.application.dto.response.PriorityDocumentResponse;
import com.stu.dormitory.modules.application.entity.ApplicationPriority;
import com.stu.dormitory.modules.application.entity.DormitoryApplication;
import com.stu.dormitory.modules.application.entity.PriorityDocument;
import com.stu.dormitory.modules.application.entity.VerificationDocument;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.PriorityCategory;
import com.stu.dormitory.modules.application.enums.VerificationDocumentType;
import com.stu.dormitory.modules.application.enums.VerificationStatus;
import com.stu.dormitory.modules.application.repository.ApplicationPriorityRepository;
import com.stu.dormitory.modules.application.repository.DormitoryApplicationRepository;
import com.stu.dormitory.modules.application.repository.PriorityDocumentRepository;
import com.stu.dormitory.modules.application.repository.VerificationDocumentRepository;
import com.stu.dormitory.modules.payment.service.BillService;
import com.stu.dormitory.modules.room.entity.StudentHousingAssignment;
import com.stu.dormitory.modules.room.service.HousingAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationReviewService {

    private final DormitoryApplicationRepository applicationRepository;
    private final VerificationDocumentRepository verificationDocumentRepository;
    private final PriorityDocumentRepository priorityDocumentRepository;
    private final ApplicationPriorityRepository applicationPriorityRepository;
    private final HousingAssignmentService housingAssignmentService;
    private final BillService billService;

    // ==================== QUERY METHODS ====================
    public List<AdminApplicationResponse> getPendingApplications() {
        List<DormitoryApplication> applications =
                applicationRepository.findByStatusOrderByPriorityScoreDescSubmittedAtAsc(ApplicationStatus.PENDING);
        return applications.stream()
                .map(app -> AdminApplicationResponse.builder()
                        .applicationCode(app.getApplicationCode())
                        .fullName(app.getFullName())
                        .cccd(app.getCccd())
                        .gender(app.getGender())
                        .email(app.getEmail())
                        .phone(app.getPhone())
                        .studentCode(app.getStudentCode())
                        .priorityScore(app.getPriorityScore())
                        .registrationType(app.getRegistrationType())
                        .status(app.getStatus())
                        .applicationPdfUrl(app.getApplicationPdfUrl())
                        .build())
                .toList();
    }

    public AdminApplicationDetailResponse getApplicationDetail(String applicationCode) {
        DormitoryApplication application = applicationRepository.findByApplicationCode(applicationCode)
                .orElseThrow(() -> new AppException("Application not found", HttpStatus.NOT_FOUND));
        return AdminApplicationDetailResponse.builder()
                .applicationCode(application.getApplicationCode())
                .cccd(application.getCccd())
                .fullName(application.getFullName())
                .gender(application.getGender())
                .email(application.getEmail())
                .phone(application.getPhone())
                .studentCode(application.getStudentCode())
                .note(application.getNote())
                .priorityScore(application.getPriorityScore())
                .registrationType(application.getRegistrationType())
                .status(application.getStatus())
                .applicationPdfUrl(application.getApplicationPdfUrl())
                .submittedAt(application.getSubmittedAt())
                .paymentDeadline(application.getPaymentDeadline())
                .revisionDeadline(application.getRevisionDeadline())
                .build();
    }

    // ==================== REVIEW APPLICATION (APPROVE / REJECT / REQUEST_SUPPLEMENT) ====================
    @Transactional
    public void reviewApplication(String applicationCode, AdminReviewRequest request) {
        DormitoryApplication application = applicationRepository.findByApplicationCode(applicationCode)
                .orElseThrow(() -> new AppException("Application not found", HttpStatus.NOT_FOUND));

        if (application.getStatus() != ApplicationStatus.PENDING
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new AppException("Application already processed", HttpStatus.BAD_REQUEST);
        }

        String action = request.getAction().trim().toUpperCase();

        switch (action) {
            case "APPROVE" -> {
                // 1. Kiểm tra normal documents bắt buộc (CCCD, PORTRAIT_PHOTO, COMMITMENT)
                validateRequiredDocuments(application);
                // 2. Kiểm tra priority documents (chỉ cần ít nhất một valid nếu có đăng ký ưu tiên)
                validatePriorityDocuments(application);

                // 3. Xếp phòng
                StudentHousingAssignment assignment = housingAssignmentService.reserveBed(application);
                if (assignment != null) {
                    billService.createAccommodationBill(assignment, BigDecimal.valueOf(500000.0));
                    application.setStatus(ApplicationStatus.WAITING_PAYMENT);
                    application.setPaymentDeadline(LocalDateTime.now().plusDays(3));
                } else {
                    application.setStatus(ApplicationStatus.WAITING_LIST);
                    application.setPaymentDeadline(null);
                }
                application.setRevisionDeadline(null);
                application.setReviewNote(request.getNote());
            }
            case "REJECT" -> {
                application.setStatus(ApplicationStatus.REJECTED);
                application.setPaymentDeadline(null);
                application.setRevisionDeadline(null);
                application.setReviewNote(request.getNote());
            }
            case "REQUEST_SUPPLEMENT" -> {
                application.setStatus(ApplicationStatus.REVISION_REQUIRED);
                application.setRevisionDeadline(LocalDateTime.now().plusDays(3));
                application.setPaymentDeadline(null);
                application.setReviewNote(request.getNote());
            }
            default -> throw new AppException("Invalid action", HttpStatus.BAD_REQUEST);
        }
        applicationRepository.save(application);
    }

    // ==================== VERIFY NORMAL DOCUMENT (ADMIN) ====================
    @Transactional
    public void verifyNormalDocument(Long documentId, VerifyDocumentRequest request) {
        VerificationDocument document = verificationDocumentRepository.findById(documentId)
                .orElseThrow(() -> new AppException("Document not found", HttpStatus.NOT_FOUND));

        DormitoryApplication application = document.getApplication();
        if (application.getStatus() != ApplicationStatus.PENDING
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW
                && application.getStatus() != ApplicationStatus.REVISION_REQUIRED) {
            throw new AppException("Cannot verify document at current status", HttpStatus.BAD_REQUEST);
        }

        document.setVerificationStatus(request.getStatus());
        document.setNote(request.getNote());
        document.setVerifiedAt(LocalDateTime.now());
        verificationDocumentRepository.save(document);
    }

    // ==================== VALIDATE REQUIRED NORMAL DOCUMENTS ====================
    private void validateRequiredDocuments(DormitoryApplication application) {
        List<VerificationDocumentType> requiredTypes = List.of(
                VerificationDocumentType.CCCD,
                VerificationDocumentType.PORTRAIT_PHOTO,
                VerificationDocumentType.COMMITMENT
        );

        List<VerificationDocument> validDocs = verificationDocumentRepository
                .findByApplication_IdAndDocumentTypeIn(application.getId(), requiredTypes)
                .stream()
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.VALID)
                .toList();

        for (VerificationDocumentType type : requiredTypes) {
            boolean hasValid = validDocs.stream().anyMatch(doc -> doc.getDocumentType() == type);
            if (!hasValid) {
                throw new AppException(
                        "Required document " + type.name() + " is not verified",
                        HttpStatus.BAD_REQUEST
                );
            }
        }
    }

    // ==================== VALIDATE PRIORITY DOCUMENTS (CHỈ CẦN ÍT NHẤT MỘT VALID) ====================
    private void validatePriorityDocuments(DormitoryApplication application) {
        List<ApplicationPriority> priorities = applicationPriorityRepository.findByApplication_Id(application.getId());
        if (priorities.isEmpty()) {
            return; // không có ưu tiên -> không cần kiểm tra
        }

        List<PriorityDocument> validDocs = priorityDocumentRepository
                .findByApplication_IdAndVerificationStatus(application.getId(), VerificationStatus.VALID);

        boolean hasAnyValid = priorities.stream().anyMatch(priority ->
                validDocs.stream().anyMatch(doc ->
                        doc.getDocumentType() == priority.getCategory().getRequiredDocumentType()
                )
        );
        if (!hasAnyValid) {
            throw new AppException("At least one valid priority document is required", HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== VERIFY PRIORITY DOCUMENT ====================
    @Transactional
    public void verifyPriorityDocument(Long documentId, VerifyPriorityDocumentRequest request) {
        PriorityDocument document = priorityDocumentRepository.findById(documentId)
                .orElseThrow(() -> new AppException("Priority document not found", HttpStatus.NOT_FOUND));

        DormitoryApplication application = document.getApplication();
        if (application.getStatus() != ApplicationStatus.PENDING
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW
                && application.getStatus() != ApplicationStatus.REVISION_REQUIRED) {
            throw new AppException("Cannot verify document at current status", HttpStatus.BAD_REQUEST);
        }

        document.setVerificationStatus(request.getStatus());
        document.setReviewNote(request.getNote());
        priorityDocumentRepository.save(document);

        recalculatePriorityScore(application);
    }

    // ==================== RECALCULATE PRIORITY SCORE ====================
    private void recalculatePriorityScore(DormitoryApplication application) {
        List<ApplicationPriority> priorities = applicationPriorityRepository.findByApplication_Id(application.getId());
        List<PriorityDocument> validDocs = priorityDocumentRepository
                .findByApplication_IdAndVerificationStatus(application.getId(), VerificationStatus.VALID);

        int maxScore = 0;
        for (ApplicationPriority priority : priorities) {
            PriorityCategory category = priority.getCategory();
            boolean hasValidProof = validDocs.stream()
                    .anyMatch(doc -> doc.getDocumentType() == category.getRequiredDocumentType());
            if (hasValidProof && category.getScore() > maxScore) {
                maxScore = category.getScore();
            }
        }
        application.setPriorityScore(maxScore);
        applicationRepository.save(application);
    }

    // ==================== GET PRIORITY DOCUMENTS ====================
    public List<PriorityDocumentResponse> getPriorityDocuments(String applicationCode) {
        List<PriorityDocument> documents = priorityDocumentRepository.findByApplication_ApplicationCode(applicationCode);
        return documents.stream()
                .map(doc -> PriorityDocumentResponse.builder()
                        .id(doc.getId())
                        .documentType(doc.getDocumentType())
                        .fileUrl(doc.getFileUrl())
                        .verificationStatus(doc.getVerificationStatus())
                        .reviewNote(doc.getReviewNote())
                        .build())
                .toList();
    }
}