package com.stu.dormitory.modules.application.service;
import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.application.entity.ApplicationPeriod;
import com.stu.dormitory.modules.application.enums.PeriodStatus;
import com.stu.dormitory.modules.application.repository.ApplicationPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ApplicationPeriodService {

    private final ApplicationPeriodRepository repository;

    public ApplicationPeriod getCurrentPeriod() {
        LocalDate today = LocalDate.now();

        ApplicationPeriod period = repository
                .findFirstByStatus(PeriodStatus.OPEN)
                .orElseThrow(() -> new AppException("Hiện tại chưa có đợt đăng ký nào được mở"));

        if (today.isBefore(period.getStartDate())) {
            throw new AppException("Đợt đăng ký " + period.getName() + " chưa đến thời gian tiếp nhận");
        }

        if (today.isAfter(period.getEndDate())) {
            throw new AppException("Đợt đăng ký " + period.getName() + " đã kết thúc");
        }

        return period;
    }
}

