package com.stu.dormitory.modules.room.repository;

import com.stu.dormitory.modules.room.entity.Bed;
import com.stu.dormitory.modules.room.enums.BedStatus;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

    Optional<Bed> findByBedCode(String bedCode);

    List<Bed> findByStatus(BedStatus status);

    /**
     * Tìm danh sách giường trống trong phòng với pessimistic lock
     * để tránh nhiều transaction cùng lúc lấy cùng một giường.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM Bed b
            WHERE b.room.id = :roomId
            AND b.status = :status
            ORDER BY b.bedCode ASC
            """)
    List<Bed> findAvailableBeds(@Param("roomId") Long roomId,
                                @Param("status") BedStatus status);
}