package com.stu.dormitory.modules.room.repository;

import com.stu.dormitory.modules.room.entity.StudentHousingAssignment;
import com.stu.dormitory.modules.room.enums.AssignmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentHousingAssignmentRepository
        extends JpaRepository<
        StudentHousingAssignment,
        Long
        > {

    List<StudentHousingAssignment>
    findByStatus(
            AssignmentStatus status
    );

    Optional<StudentHousingAssignment> findByApplication_Student_IdAndStatus(Long studentId, AssignmentStatus status);
}