package com.stu.dormitory.modules.student.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.student.dto.request.UpdateProfileRequest;
import com.stu.dormitory.modules.student.dto.response.UserProfileResponse;
import com.stu.dormitory.modules.student.entity.Student;
import com.stu.dormitory.modules.student.repository.StudentRepository;
import com.stu.dormitory.modules.upload.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final CloudinaryService cloudinaryService;

    private UserAccount getCurrentUserAccount() {
        // Lấy UserAcount từ bên JWT
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        // Kiểm tra có UserAcount không và có phải là tài khoản không
        if(authentication== null || ! (authentication.getPrincipal() instanceof UserAccount account) )
        {
            throw new AppException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        return account;
    }

   public UserProfileResponse getMyProfile()
   {
       UserAccount account = getCurrentUserAccount();

       Student student = Optional.ofNullable(account.getStudent())
               .orElseThrow( () ->
                 new AppException("Student profile not found",HttpStatus.NOT_FOUND)
               );

       return UserProfileResponse.builder()
               .id(student.getId())
               .username(account.getUsername())
               .role(account.getRole())
               .studentCode(student.getStudentCode())
               .fullName(student.getFullName())
               .cccd(student.getCccd())
               .gender(student.getGender())
               .dateOfBirth(student.getDateOfBirth())
               .phone(student.getPhone())
               .email(student.getEmail())
               .faculty(student.getFaculty())
               .course(student.getCourse())
               .avatarUrl(student.getAvatarUrl())
               .build();
   }

    @Transactional
    public void updateProfile(UserAccount account, UpdateProfileRequest request) {
        Student student = Optional.ofNullable(account.getStudent())
                .orElseThrow(() -> new AppException("Student not found", HttpStatus.NOT_FOUND));

        boolean updated = false;

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            student.setFullName(request.getFullName());
            updated = true;
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            student.setPhone(request.getPhone());
            updated = true;
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!student.getEmail().equals(request.getEmail())
                    && studentRepository.existsByEmail(request.getEmail())) {
                throw new AppException("Email already exists", HttpStatus.BAD_REQUEST);
            }
            student.setEmail(request.getEmail());
            updated = true;
        }

        if (updated) {
            studentRepository.save(student);
        }
    }



    @Transactional
    public String uploadAvatar(UserAccount account, MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException("No file uploaded", HttpStatus.BAD_REQUEST);
        }

        Student student = Optional.ofNullable(account.getStudent())
                .orElseThrow(() -> new AppException("Student not found", HttpStatus.NOT_FOUND));

        try {
            String avatarUrl = cloudinaryService.uploadFile(file, "dormitory/avatars");
            student.setAvatarUrl(avatarUrl);
            studentRepository.save(student);
            return avatarUrl;
        } catch (IOException e) {
            log.error("Upload avatar failed", e);
            throw new AppException("Failed to upload avatar", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
