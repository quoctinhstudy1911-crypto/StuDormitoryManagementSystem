package com.stu.dormitory.modules.upload.service;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(
            File file,
            String folder
    ) {

        try {

            Map uploadResult =
                    cloudinary.uploader()
                            .upload(
                                    file,
                                    Map.of(
                                            "folder",
                                            folder
                                    )
                            );

            return uploadResult
                    .get("secure_url")
                    .toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Upload file failed"
            );
        }
    }
}