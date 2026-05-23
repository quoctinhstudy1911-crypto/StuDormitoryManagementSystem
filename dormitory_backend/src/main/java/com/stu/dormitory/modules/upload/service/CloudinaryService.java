package com.stu.dormitory.modules.upload.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.stu.dormitory.common.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import java.io.File;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload file lên Cloudinary
     * @param file file cần upload (phải tồn tại và có thể đọc được)
     * @param folder thư mục trên Cloudinary (ví dụ: "applications", "avatars")
     * @return secure URL của file đã upload
     * @throws AppException nếu upload thất bại
     */
    public String uploadFile(File file, String folder) {
        if (file == null || !file.exists() || !file.canRead()) {
            log.error("Invalid file for upload: {}", file != null ? file.getAbsolutePath() : "null");
            throw new AppException("Invalid file", HttpStatus.BAD_REQUEST);
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file, Map.of("folder", folder));
            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new AppException("Upload response missing secure_url", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("File uploaded successfully: {}", secureUrl);
            return secureUrl.toString();
        } catch (Exception e) {
            log.error("Cloudinary upload failed for file: {}", file.getAbsolutePath(), e);
            throw new AppException("Upload file failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String uploadFile(MultipartFile multipartFile, String folder) throws IOException {
        File tempFile = File.createTempFile("upload_", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        try {
            return uploadFile(tempFile, folder);
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

}