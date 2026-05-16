package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.VerificationDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationDocumentRepository
        extends JpaRepository<VerificationDocument, Long> {

    List<VerificationDocument>
    findByApplication_Id(Long applicationId);
}