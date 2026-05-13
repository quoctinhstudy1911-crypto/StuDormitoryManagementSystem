package com.stu.dormitory.modules.document.controller;

import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.document.dto.DocumentRequestDTO;
import com.stu.dormitory.modules.document.dto.DocumentResponseDTO;
import com.stu.dormitory.modules.document.entity.ApplicationDocument;
import com.stu.dormitory.modules.document.enums.DocumentStatus;
import com.stu.dormitory.modules.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> upload(@RequestBody @Valid DocumentRequestDTO req) {

        ApplicationDocument doc = documentService.upload(
                req.getApplicationId(),
                req.getType(),
                req.getFileUrl()
        );

        DocumentResponseDTO res = new DocumentResponseDTO(
                doc.getId(),
                doc.getType().name(),
                doc.getFileUrl(),
                doc.getStatus().name(),
                doc.getApplication().getId()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Upload thành công", res));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<?>> verify(
            @PathVariable Long id,
            @RequestParam DocumentStatus status
    ) {

        documentService.verify(id, status);

        return ResponseEntity.ok(new ApiResponse<>(true, "Xác minh thành công", null));
    }

    @GetMapping("/application/{appId}")
    public ResponseEntity<ApiResponse<?>> getByApplication(@PathVariable Long appId) {

        List<DocumentResponseDTO> res = documentService.getByApplication(appId)
                .stream()
                .map(doc -> new DocumentResponseDTO(
                        doc.getId(),
                        doc.getType().name(),
                        doc.getFileUrl(),
                        doc.getStatus().name(),
                        doc.getApplication().getId()
                ))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "Success", res));
    }
}