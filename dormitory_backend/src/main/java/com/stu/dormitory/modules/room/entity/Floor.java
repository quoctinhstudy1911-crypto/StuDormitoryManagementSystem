package com.stu.dormitory.modules.room.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.room.enums.OccupancyPolicy;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "floors",

uniqueConstraints = {
@UniqueConstraint(
        columnNames = {
                "building_id",
                "floor_number"
        }
)
    }


            )
public class Floor extends BaseEntity {


    /**
     * 1, 2, 3...
     */
    @Column(
            name = "floor_number",
            nullable = false
    )
    private Integer floorNumber;

    /**
     * MALE / FEMALE / MIXED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OccupancyPolicy occupancyPolicy;

    /**
     * Building chứa tầng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "building_id",
            nullable = false
    )
    private Building building;

}
