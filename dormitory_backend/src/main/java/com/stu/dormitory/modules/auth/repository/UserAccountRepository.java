package com.stu.dormitory.modules.auth.repository;

import com.stu.dormitory.modules.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    // Tìm kiếm tài khoản người dùng theo tên đăng nhập
   Optional<UserAccount> findByUsername(String username);

   Optional<UserAccount> findByStudentId(Long studentId);

}
