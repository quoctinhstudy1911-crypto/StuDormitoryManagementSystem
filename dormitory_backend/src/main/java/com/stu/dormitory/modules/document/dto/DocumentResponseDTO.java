package com.stu.dormitory.modules.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentResponseDTO {

    private Long id;

    private String type;

    private String fileUrl;

    private String status;

    private Long applicationId;
}