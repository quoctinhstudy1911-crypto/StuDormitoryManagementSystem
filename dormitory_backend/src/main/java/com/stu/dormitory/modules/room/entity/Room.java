package com.stu.dormitory.modules.room.entity;

import com.stu.dormitory.common.entity.BaseEntity;
import com.stu.dormitory.modules.room.enums.RoomStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "rooms",

uniqueConstraints = {
@UniqueConstraint(
        columnNames = {
                "floor_id",
                "room_code"
        }
)
    }


            )
public class Room extends BaseEntity {


    /**
     * A101, A102...
     */
    @Column(
            name = "room_code",
            nullable = false,
            length = 30
    )
    private String roomCode;

    /**
     * Số giường tối đa
     */
    @Column(nullable = false)
    private Integer capacity;

    /**
     * Giường đang sử dụng
     */
    @Column(nullable = false)
    private Integer occupiedBeds = 0;

    /**
     * Giá phòng / tháng
     */
    @Column(nullable = false)
    private BigDecimal monthlyFee;

    /**
     * AVAILABLE / FULL / MAINTENANCE...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status =
            RoomStatus.AVAILABLE;

    /**
     * Floor chứa phòng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "floor_id",
            nullable = false
    )
    private Floor floor;


}
