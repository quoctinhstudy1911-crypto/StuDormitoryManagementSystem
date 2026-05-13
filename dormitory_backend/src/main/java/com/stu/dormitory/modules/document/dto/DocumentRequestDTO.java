package com.stu.dormitory.modules.document.dto;

import com.stu.dormitory.modules.document.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentRequestDTO {

    @NotNull(message = "ApplicationId không được null")
    private Long applicationId;

    @NotNull(message = "Loại giấy tờ không được null")
    private DocumentType type;

    @NotBlank(message = "File URL không được để trống")
    private String fileUrl;
}