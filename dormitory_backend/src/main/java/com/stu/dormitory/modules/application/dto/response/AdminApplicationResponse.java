package com.stu.dormitory.modules.application.dto.response;

import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.application.enums.RegistrationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class AdminApplicationResponse {
    private String applicationCode;

    private String fullName;

    private String cccd;

    private Gender gender;

    private String email;

    private String phone;

    private String studentCode;

    private Integer priorityScore;

    private RegistrationType registrationType;

    private ApplicationStatus status;

    private String applicationPdfUrl;
}
/**
 * GIẢI THÍCH VỀ HIBERNATE PROXY:
 * -----------------------------------------------------------------------
 * 1. Định nghĩa: Là một đối tượng "giả" (Placeholder) mà Hibernate tạo ra
 *    cho các mối quan hệ FetchType.LAZY.
 * 2. Cơ chế: Proxy chỉ chứa ID của đối tượng liên kết và sẽ "tự nạp" (Lazy Loading)
 *    dữ liệu thật khi các phương thức getter được gọi lần đầu.
 * 3. Vấn đề: Các thư viện JSON (như Jackson) không thể đọc được cấu trúc
 *    của Proxy Object dẫn đến lỗi serialization.
 * 4. Khắc phục: Sử dụng DTO để tách biệt dữ liệu cần trả về khỏi các Proxy của Hibernate.
 */
