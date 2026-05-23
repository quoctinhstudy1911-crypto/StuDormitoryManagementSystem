package com.stu.dormitory.modules.room.repository;

import com.stu.dormitory.modules.room.entity.Floor;
import com.stu.dormitory.modules.room.enums.OccupancyPolicy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FloorRepository
        extends JpaRepository<Floor, Long> {

    List<Floor>
    findByOccupancyPolicy(
            OccupancyPolicy occupancyPolicy
    );
}