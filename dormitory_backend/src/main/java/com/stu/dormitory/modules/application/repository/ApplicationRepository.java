package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.Application;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByStudentIdAndPeriodId(Long studentId, Long periodId);

    Optional<Application> findByStudentIdAndPeriodId(Long studentId, Long periodId);

    @Query("""
        SELECT a FROM Application a
        WHERE a.status = :status
        ORDER BY 
            a.isPriorityManual DESC,
            a.score DESC,
            a.submittedAt ASC
    """)
    List<Application> findApprovalQueue(ApplicationStatus status);

    List<Application> findByStatus(ApplicationStatus status);

    List<Application> findByPeriodId(Long periodId);

    long countByStatus(ApplicationStatus status);
}
