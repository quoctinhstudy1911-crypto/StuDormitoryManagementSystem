package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.RegistrationEligibility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationEligibilityRepository
        extends JpaRepository<RegistrationEligibility, Long> {

    boolean existsByCccdAndRegistrationPeriod_Id(
            String cccd,
            Long registrationPeriodId
    );
}