package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.common.entity.BaseEntity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "registration_eligibilities",

        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "cccd",
                                "registration_period_id"
                        }
                )
        }
)
@Getter
@Setter
public class RegistrationEligibility
        extends BaseEntity {

    @Column(nullable = false)
    private String cccd;

    @ManyToOne
    @JoinColumn(nullable = false)
    private RegistrationPeriod registrationPeriod;
}