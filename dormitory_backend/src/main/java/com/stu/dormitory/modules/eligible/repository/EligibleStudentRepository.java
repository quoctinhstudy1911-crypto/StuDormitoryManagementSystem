package com.stu.dormitory.modules.eligible.repository;

import com.stu.dormitory.modules.eligible.entity.EligibleStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EligibleStudentRepository extends JpaRepository<EligibleStudent, Long> {
    // Tìm kiếm sinh viên đủ điều kiện theo CCCD và kỳ hạn
    Optional<EligibleStudent> findByCccdAndPeriodId(String cccd, Long periodId);
}
