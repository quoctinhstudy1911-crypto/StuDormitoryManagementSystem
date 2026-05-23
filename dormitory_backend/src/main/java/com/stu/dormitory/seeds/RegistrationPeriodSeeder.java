package com.stu.dormitory.seeds;

import com.stu.dormitory.modules.application.entity.RegistrationPeriod;
import com.stu.dormitory.modules.application.enums.RegistrationTarget;
import com.stu.dormitory.modules.application.repository.RegistrationPeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class RegistrationPeriodSeeder implements CommandLineRunner {

    private final RegistrationPeriodRepository periodRepository;

    @Override
    public void run(String... args) {
        if (periodRepository.count() > 0) {
            log.info("Registration period already exists, skip seeding.");
            return;
        }

        log.info("Seeding registration period...");

        RegistrationPeriod period = new RegistrationPeriod();
        period.setName("Đợt đăng ký KTX - Học kỳ 1 năm 2026");
        period.setTarget(RegistrationTarget.ALL);
        period.setStartDate(LocalDateTime.now().minusDays(1));
        period.setEndDate(LocalDateTime.now().plusMonths(1));
        period.setQuota(500);
        period.setActive(true);
        periodRepository.save(period);

        log.info("Seeded active registration period: {}", period.getName());
    }
}