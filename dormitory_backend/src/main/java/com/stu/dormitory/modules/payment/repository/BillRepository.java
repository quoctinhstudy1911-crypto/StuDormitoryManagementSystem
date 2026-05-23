package com.stu.dormitory.modules.payment.repository;

import com.stu.dormitory.modules.payment.entity.Bill;
import com.stu.dormitory.modules.payment.enums.BillStatus;
import com.stu.dormitory.modules.payment.enums.BillType;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository
        extends JpaRepository<Bill, Long> {

    List<Bill>
    findByStatus(BillStatus status);

    List<Bill>
    findByBillType(BillType billType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bill b WHERE b.id = :id")
    Optional<Bill> findByIdForUpdate(@Param("id") Long id);

    List<Bill> findByStatusIn(List<BillStatus> statuses);
}