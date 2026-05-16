package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.PriorityDocument;

import com.stu.dormitory.modules.application.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriorityDocumentRepository
        extends JpaRepository<PriorityDocument, Long> {

    List<PriorityDocument>
    findByApplication_ApplicationCode(
            String applicationCode
    );

    List<PriorityDocument>
    findByApplication_IdAndVerificationStatus(
            Long applicationId,
            VerificationStatus verificationStatus
    );
}