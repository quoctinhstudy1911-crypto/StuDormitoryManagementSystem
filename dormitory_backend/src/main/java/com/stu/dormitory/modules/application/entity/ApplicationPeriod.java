package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.application.enums.PeriodStatus;
import com.stu.dormitory.modules.application.enums.PeriodType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class ApplicationPeriod extends BaseEntity {

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PeriodType type; // FRESHMAN / GENERAL

    @Enumerated(EnumType.STRING)
    private PeriodStatus status; // OPEN / CLOSED
}
