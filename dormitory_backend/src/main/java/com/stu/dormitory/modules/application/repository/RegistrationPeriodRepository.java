package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.RegistrationPeriod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RegistrationPeriodRepository
        extends JpaRepository<RegistrationPeriod, Long> {

    Optional<RegistrationPeriod>
    findByActiveTrueAndStartDateBeforeAndEndDateAfter(
            LocalDateTime now1,
            LocalDateTime now2
    );
}