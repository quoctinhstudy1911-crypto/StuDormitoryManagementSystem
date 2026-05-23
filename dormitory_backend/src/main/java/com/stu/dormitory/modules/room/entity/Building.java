package com.stu.dormitory.modules.room.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.room.enums.BuildingStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "buildings")
public class Building extends BaseEntity {

    /**
     * A / B / C...
     */
    @Column(
            nullable = false,
            unique = true,
            length = 20
    )
    private String code;

    /**
     * KTX A...
     */
    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    /**
     * Mô tả tòa nhà
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * ACTIVE / INACTIVE / MAINTENANCE...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuildingStatus status =
            BuildingStatus.ACTIVE;


}
