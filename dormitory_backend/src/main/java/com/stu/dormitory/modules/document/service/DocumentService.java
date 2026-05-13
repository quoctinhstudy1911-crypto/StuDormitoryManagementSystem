package com.stu.dormitory.modules.document.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.application.entity.Application;
import com.stu.dormitory.modules.application.repository.ApplicationRepository;
import com.stu.dormitory.modules.document.entity.ApplicationDocument;
import com.stu.dormitory.modules.document.enums.DocumentStatus;
import com.stu.dormitory.modules.document.enums.DocumentType;
import com.stu.dormitory.modules.document.repository.ApplicationDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ApplicationDocumentRepository documentRepo;
    private final ApplicationRepository applicationRepo;

    public ApplicationDocument upload(Long appId, DocumentType type, String fileUrl) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy đơn"));

        boolean existed = documentRepo.findByApplicationId(appId)
                .stream()
                .anyMatch(d -> d.getType() == type);

        if (existed) {
            throw new AppException("Đã tồn tại loại giấy tờ này");
        }

        ApplicationDocument doc = new ApplicationDocument();
        doc.setApplication(app);
        doc.setType(type);
        doc.setFileUrl(fileUrl);
        doc.setStatus(DocumentStatus.PENDING);

        return documentRepo.save(doc);
    }

    public void verify(Long docId, DocumentStatus status) {

        ApplicationDocument doc = documentRepo.findById(docId)
                .orElseThrow(() -> new AppException("Không tìm thấy hồ sơ"));

        if (doc.getStatus() != DocumentStatus.PENDING) {
            throw new AppException("Đã verify rồi");
        }

        doc.setStatus(status);
        documentRepo.save(doc);
    }

    public List<ApplicationDocument> getByApplication(Long appId) {
        return documentRepo.findByApplicationId(appId);
    }
}