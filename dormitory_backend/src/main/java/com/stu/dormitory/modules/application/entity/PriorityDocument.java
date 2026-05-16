package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.modules.application.enums.PriorityDocumentType;
import com.stu.dormitory.modules.application.enums.VerificationStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "priority_documents")
public class PriorityDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PriorityDocumentType documentType;

    @Column(nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus =
            VerificationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private DormitoryApplication application;
}