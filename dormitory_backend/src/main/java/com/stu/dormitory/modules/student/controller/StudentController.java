package com.stu.dormitory.modules.student.controller;

import com.stu.dormitory.common.response.ApiResponse;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.room.dto.response.CurrentRoomResponse;
import com.stu.dormitory.modules.room.service.HousingAssignmentService;
import com.stu.dormitory.modules.student.dto.request.UpdateProfileRequest;
import com.stu.dormitory.modules.student.dto.response.UserProfileResponse;
import com.stu.dormitory.modules.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final HousingAssignmentService housingAssignmentService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile() {

        UserProfileResponse response =
                studentService.getMyProfile();

        return new ApiResponse<>(
                true,
                "Get profile successfully",
                response
        );
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<?> updateProfile(@AuthenticationPrincipal UserAccount account,
                                        @Valid @RequestBody UpdateProfileRequest request) {
        studentService.updateProfile(account, request);
        return new ApiResponse<>(true, "Profile updated successfully", null);

    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<String> uploadAvatar(@AuthenticationPrincipal UserAccount account,
                                            @RequestParam("file") MultipartFile file) {
        String avatarUrl = studentService.uploadAvatar(account, file);
        return new ApiResponse<>(true, "Avatar updated successfully", avatarUrl);
    }


}

