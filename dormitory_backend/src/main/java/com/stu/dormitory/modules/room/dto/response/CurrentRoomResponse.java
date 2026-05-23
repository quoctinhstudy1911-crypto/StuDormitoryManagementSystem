package com.stu.dormitory.modules.room.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CurrentRoomResponse {
    private String building;         // Tòa nhà (ví dụ "A")
    private Integer floor;           // Tầng (ví dụ 1)
    private String roomCode;         // Mã phòng (ví dụ "A101")
    private String bedCode;          // Mã giường (ví dụ "A101-B01")
    private String assignmentStatus; // OCCUPIED, RESERVED, ...
    private LocalDateTime checkInAt; // Ngày nhận phòng
}