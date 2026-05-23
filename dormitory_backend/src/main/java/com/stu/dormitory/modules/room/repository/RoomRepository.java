package com.stu.dormitory.modules.room.repository;

import com.stu.dormitory.modules.room.entity.Room;
import com.stu.dormitory.modules.room.enums.OccupancyPolicy;
import com.stu.dormitory.modules.room.enums.RoomStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository
        extends JpaRepository<Room, Long> {

    Optional<Room>
    findByRoomCode(String roomCode);

    List<Room>
    findByStatus(RoomStatus status);

    @Query("""
    SELECT r
    FROM Room r
    WHERE r.floor.occupancyPolicy = :policy
    AND r.status = :status
    ORDER BY r.occupiedBeds ASC
""")
    List<Room> findAvailableRoomsByPolicy(
            @Param("policy")
            OccupancyPolicy policy,

            @Param("status")
            RoomStatus status
    );
}