package com.stu.dormitory.modules.auth.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.auth.enums.Role;
import com.stu.dormitory.modules.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_account") // Đặt tên bảng trong cơ sở dữ liệu là "user_account"
@Getter @Setter
public class UserAccount extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username; // Tên đăng nhập, duy nhất và không được để trống (CCCD)

    private String password; // Mật khẩu đã được mã hóa (hashed)

    @Column(nullable = false)
    private Boolean isActive = true; // Trạng thái tài khoản (kích hoạt hay không)

    @Enumerated(EnumType.STRING)
    private Role role; // Vai trò của người dùng (USER, STAFF, ADMIN)

    @OneToOne
    @JoinColumn(name = "student_id", nullable = true)
    private Student student; // Liên kết với thực thể Student (nếu có)

}
