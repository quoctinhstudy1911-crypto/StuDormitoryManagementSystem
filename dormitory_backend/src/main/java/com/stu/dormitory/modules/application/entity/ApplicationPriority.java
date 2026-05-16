package com.stu.dormitory.modules.application.entity;

import com.stu.dormitory.modules.application.enums.PriorityCategory;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "application_priorities")
public class ApplicationPriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PriorityCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private DormitoryApplication application;
}