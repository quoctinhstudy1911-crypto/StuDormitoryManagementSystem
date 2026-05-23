package com.stu.dormitory.seeds;

import com.stu.dormitory.modules.application.entity.RegistrationEligibility;
import com.stu.dormitory.modules.application.entity.RegistrationPeriod;
import com.stu.dormitory.modules.application.repository.RegistrationEligibilityRepository;
import com.stu.dormitory.modules.application.repository.RegistrationPeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class EligibilitySeeder implements CommandLineRunner {

    private final RegistrationPeriodRepository periodRepository;
    private final RegistrationEligibilityRepository eligibilityRepository;

    @Override
    public void run(String... args) {
        if (eligibilityRepository.count() > 0) {
            log.info("Eligibility list already exists, skip seeding.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        RegistrationPeriod period = periodRepository
                .findByActiveTrueAndStartDateBeforeAndEndDateAfter(now, now)
                .orElseThrow(() -> new RuntimeException(
                        "No active registration period. Please run RegistrationPeriodSeeder first and ensure it sets an active period."
                ));

        // Danh sách CCCD được phép đăng ký (phải đồng bộ với UserAndStudentSeeder)
        List<String> eligibleCccds = List.of(
                "060204002557",   // sinh viên mẫu (Nam)
                "079204001234",   // sinh viên nữ
                "079204005678",   // sinh viên nam
                "079204009999"    // sinh viên nữ (mới)
        );

        log.info("Seeding eligibility for {} CCCDs using period ID: {}", eligibleCccds.size(), period.getId());

        for (String cccd : eligibleCccds) {
            RegistrationEligibility eligibility = new RegistrationEligibility();
            eligibility.setCccd(cccd);
            eligibility.setRegistrationPeriod(period);
            eligibilityRepository.save(eligibility);
        }

        log.info("Seeded eligibility list successfully.");
    }
}