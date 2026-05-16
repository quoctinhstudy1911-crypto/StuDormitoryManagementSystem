package com.stu.dormitory.modules.application.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.application.dto.request.AdminReviewRequest;
import com.stu.dormitory.modules.application.dto.request.ReviewPriorityDocumentRequest;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationDetailResponse;
import com.stu.dormitory.modules.application.dto.response.AdminApplicationResponse;
import com.stu.dormitory.modules.application.dto.response.PriorityDocumentResponse;
import com.stu.dormitory.modules.application.entity.ApplicationPriority;
import com.stu.dormitory.modules.application.entity.DormitoryApplication;
import com.stu.dormitory.modules.application.entity.PriorityDocument;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.PriorityCategory;
import com.stu.dormitory.modules.application.enums.PriorityDocumentType;
import com.stu.dormitory.modules.application.enums.VerificationStatus;
import com.stu.dormitory.modules.application.repository.ApplicationPriorityRepository;
import com.stu.dormitory.modules.application.repository.DormitoryApplicationRepository;
import com.stu.dormitory.modules.application.repository.PriorityDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationReviewService {

    private final DormitoryApplicationRepository applicationRepository;
    private final PriorityDocumentRepository priorityDocumentRepository;
    private final ApplicationPriorityRepository applicationPriorityRepository;

    public List<AdminApplicationResponse>
    getPendingApplications() {

        List<DormitoryApplication> applications =
                applicationRepository
                        .findByStatusOrderByPriorityScoreDescSubmittedAtAsc(
                                ApplicationStatus.PENDING
                        );

        return applications
                .stream()
                .map(application ->

                        AdminApplicationResponse
                                .builder()
                                .applicationCode(
                                        application.getApplicationCode()
                                )
                                .fullName(
                                        application.getFullName()
                                )
                                .cccd(
                                        application.getCccd()
                                )
                                .gender(
                                        application.getGender()
                                )
                                .email(
                                        application.getEmail()
                                )
                                .phone(
                                        application.getPhone()
                                )
                                .studentCode(
                                        application.getStudentCode()
                                )
                                .priorityScore(
                                        application.getPriorityScore()
                                )
                                .registrationType(
                                        application.getRegistrationType()
                                )
                                .status(
                                        application.getStatus()
                                )
                                .applicationPdfUrl(
                                        application.getApplicationPdfUrl()
                                )
                                .build()

                )
                .toList();
    }

    public AdminApplicationDetailResponse getApplicationDetail(String applicationCode) {
        DormitoryApplication application =
                applicationRepository.findByApplicationCode(applicationCode)
                        .orElseThrow(() ->
                                new AppException("Application not found", HttpStatus.NOT_FOUND)
                        );
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

    @Transactional
    public void reviewApplication(
            String applicationCode,
            AdminReviewRequest request
    ) {

        DormitoryApplication application =
                applicationRepository
                        .findByApplicationCode(applicationCode)
                        .orElseThrow(() ->
                                new AppException(
                                        "Application not found",
                                        HttpStatus.NOT_FOUND
                                ));

        // Không cho xử lý lại
        if (application.getStatus() != ApplicationStatus.PENDING
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW) {

            throw new AppException(
                    "Application already processed",
                    HttpStatus.BAD_REQUEST
            );
        }

        String action =
                request.getAction()
                        .trim()
                        .toUpperCase();

        switch (action) {

            case "APPROVE" -> {

                if (application.getPriorityScore() == 0
                        && !applicationPriorityRepository
                        .findByApplication_Id(
                                application.getId()
                        ).isEmpty()) {

                    throw new AppException(
                            "Priority documents have not been verified",
                            HttpStatus.BAD_REQUEST
                    );
                }

                application.setStatus(
                        ApplicationStatus.WAITING_PAYMENT
                );

                application.setPaymentDeadline(
                        LocalDateTime.now()
                                .plusDays(3)
                );

                application.setRevisionDeadline(null);

                application.setReviewNote(
                        request.getNote()
                );
            }

            case "REJECT" -> {

                application.setStatus(
                        ApplicationStatus.REJECTED
                );

                application.setPaymentDeadline(null);
                application.setRevisionDeadline(null);

                application.setReviewNote(
                        request.getNote()
                );
            }

            case "REQUEST_SUPPLEMENT" -> {

                application.setStatus(
                        ApplicationStatus.REVISION_REQUIRED
                );

                application.setRevisionDeadline(
                        LocalDateTime.now()
                                .plusDays(3)
                );

                application.setPaymentDeadline(null);

                application.setReviewNote(
                        request.getNote()
                );
            }

            default -> throw new AppException(
                    "Invalid action",
                    HttpStatus.BAD_REQUEST
            );
        }

        applicationRepository.save(application);
    }
    private boolean isMatchingDocument(
            PriorityCategory category,
            PriorityDocument document
    ) {

        return switch (category) {

            case POOR_HOUSEHOLD ->

                    document.getDocumentType()
                            == PriorityDocumentType
                            .POVERTY_CERTIFICATE;

            case MARTYR_CHILD ->

                    document.getDocumentType()
                            == PriorityDocumentType
                            .MARTYR_CERTIFICATE;

            case ETHNIC_MINORITY ->

                    document.getDocumentType()
                            == PriorityDocumentType
                            .ETHNIC_CERTIFICATE;

            case DISABLED_STUDENT ->

                    document.getDocumentType()
                            == PriorityDocumentType
                            .DISABILITY_CERTIFICATE;

            case ORPHAN ->

                    document.getDocumentType()
                            == PriorityDocumentType
                            .ORPHAN_CERTIFICATE;

            case REMOTE_AREA ->

                    document.getDocumentType()
                            == PriorityDocumentType
                            .REMOTE_AREA_CERTIFICATE;

            default -> false;
        };
    }
    private void recalculatePriorityScore(
            DormitoryApplication application
    ) {

        List<ApplicationPriority> priorities =
                applicationPriorityRepository
                        .findByApplication_Id(
                                application.getId()
                        );

        // Lấy toàn bộ proof đã VALID
        List<PriorityDocument> validDocuments =
                priorityDocumentRepository
                        .findByApplication_IdAndVerificationStatus(
                                application.getId(),
                                VerificationStatus.VALID
                        );

        int maxScore = 0;

        for (ApplicationPriority priority : priorities) {

            PriorityCategory category =
                    priority.getCategory();

            boolean hasValidProof =
                    validDocuments.stream()

                            .anyMatch(document ->

                                    isMatchingDocument(
                                            category,
                                            document
                                    )
                            );

            if (hasValidProof
                    && category.getScore() > maxScore) {

                maxScore =
                        category.getScore();
            }
        }

        application.setPriorityScore(
                maxScore
        );

        applicationRepository.save(application);
    }

    @Transactional
    public void verifyPriorityDocument(
            Long documentId,
            ReviewPriorityDocumentRequest request
    ) {

        PriorityDocument document =
                priorityDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Priority document not found",
                                        HttpStatus.NOT_FOUND
                                ));

        document.setVerificationStatus(
                request.getVerificationStatus()
        );

        if (request.getVerificationStatus() == VerificationStatus.VALID) {

            document.setRejectReason(null);

        } else {

            document.setRejectReason(
                    request.getRejectReason()
            );
        }

        recalculatePriorityScore(
                document.getApplication()
        );

        priorityDocumentRepository.save(document);
    }

    public List<PriorityDocumentResponse>
    getPriorityDocuments(
            String applicationCode
    ) {

        List<PriorityDocument> documents =
                priorityDocumentRepository
                        .findByApplication_ApplicationCode(
                                applicationCode
                        );

        return documents
                .stream()
                .map(document ->

                        PriorityDocumentResponse
                                .builder()

                                .id(
                                        document.getId()
                                )

                                .documentType(
                                        document.getDocumentType()
                                )

                                .fileUrl(
                                        document.getFileUrl()
                                )

                                .verificationStatus(
                                        document.getVerificationStatus()
                                )

                                .rejectReason(
                                        document.getRejectReason()
                                )

                                .build()

                )
                .toList();
    }
}