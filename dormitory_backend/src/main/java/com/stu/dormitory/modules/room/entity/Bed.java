package com.stu.dormitory.modules.room.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.room.enums.BedStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "beds",


uniqueConstraints = {
@UniqueConstraint(
        columnNames = {
                "room_id",
                "bed_code"
        }
)
    }


            )
public class Bed extends BaseEntity {


    /**
     * A101-B01
     */
    @Column(
            name = "bed_code",
            nullable = false,
            length = 30
    )
    private String bedCode;

    /**
     * AVAILABLE / RESERVED / OCCUPIED...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedStatus status =
            BedStatus.AVAILABLE;

    /**
     * Room chứa giường
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "room_id",
            nullable = false
    )
    private Room room;


}
