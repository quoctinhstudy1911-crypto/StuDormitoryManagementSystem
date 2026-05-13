package com.stu.dormitory.modules.application.service;

import com.stu.dormitory.modules.application.entity.Application;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class ExpirationService {

    private final ApplicationRepository applicationRepository;

    /**
     * Kiểm tra và đánh dấu các đơn APPROVED đã quá hạn check-in (3 ngày)
     * Chạy mỗi 30 phút
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void checkAndMarkExpiredApplications() {
        log.info("Starting expiration check...");

        List<Application> approvedApps = applicationRepository.findByStatus(ApplicationStatus.APPROVED);

        int expiredCount = 0;
        for (Application app : approvedApps) {
            if (app.getPaymentDeadline() != null && LocalDateTime.now().isAfter(app.getPaymentDeadline())) {
                app.setStatus(ApplicationStatus.EXPIRED);
                applicationRepository.save(app);
                expiredCount++;
                log.info("Marked application {} as EXPIRED", app.getId());
            }
        }

        log.info("Expiration check completed. {} applications marked as expired.", expiredCount);
    }

    /**
     * Lấy tất cả các đơn hết hạn
     */
    public List<Application> getExpiredApplications() {
        return applicationRepository.findByStatus(ApplicationStatus.EXPIRED);
    }

    /**
     * Kiểm tra xem một đơn cụ thể đã hết hạn chưa
     */
    public boolean isExpired(Application app) {
        if (app.getStatus() != ApplicationStatus.APPROVED || app.getPaymentDeadline() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(app.getPaymentDeadline());
    }
}

