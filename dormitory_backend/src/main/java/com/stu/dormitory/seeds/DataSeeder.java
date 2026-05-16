package com.stu.dormitory.config;

import com.stu.dormitory.modules.application.entity.RegistrationEligibility;
import com.stu.dormitory.modules.application.entity.RegistrationPeriod;

import com.stu.dormitory.modules.application.enums.RegistrationTarget;

import com.stu.dormitory.modules.application.repository.RegistrationEligibilityRepository;
import com.stu.dormitory.modules.application.repository.RegistrationPeriodRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder
        implements CommandLineRunner {

    private final RegistrationPeriodRepository
            registrationPeriodRepository;

    private final RegistrationEligibilityRepository
            eligibilityRepository;

    @Override
    public void run(String... args) {

        seedRegistrationPeriod();

        seedEligibility();
    }

    private void seedRegistrationPeriod() {

        if (registrationPeriodRepository.count() > 0) {
            return;
        }

        RegistrationPeriod period =
                new RegistrationPeriod();

        period.setName(
                "Đợt Tân Sinh Viên 2026"
        );

        period.setTarget(
                RegistrationTarget.FRESHMAN
        );

        period.setStartDate(
                LocalDateTime.now().minusDays(1)
        );

        period.setEndDate(
                LocalDateTime.now().plusDays(7)
        );

        period.setQuota(500);

        period.setActive(true);

        registrationPeriodRepository.save(period);
    }

    private void seedEligibility() {

        if (eligibilityRepository.count() > 0) {
            return;
        }

        RegistrationPeriod period =
                registrationPeriodRepository
                        .findAll()
                        .get(0);

        RegistrationEligibility eligibility =
                new RegistrationEligibility();

        eligibility.setCccd(
                "079204001234"
        );

        eligibility.setRegistrationPeriod(period);

        eligibilityRepository.save(eligibility);
    }
}