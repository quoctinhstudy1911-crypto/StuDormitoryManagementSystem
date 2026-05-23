package com.stu.dormitory.modules.student.repository;

import com.stu.dormitory.modules.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository  extends JpaRepository<Student, Long> {
    // Tìm kiếm sinh viên theo CCCD
   Optional<Student> findByCccd(String cccd);

    boolean existsByEmail(String email);
}
