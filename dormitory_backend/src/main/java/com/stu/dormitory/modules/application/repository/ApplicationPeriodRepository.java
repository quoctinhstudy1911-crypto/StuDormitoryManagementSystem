package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.ApplicationPeriod;
import com.stu.dormitory.modules.application.enums.PeriodStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationPeriodRepository extends JpaRepository<ApplicationPeriod, Long> {

    Optional<ApplicationPeriod> findFirstByStatus(PeriodStatus status);

}
