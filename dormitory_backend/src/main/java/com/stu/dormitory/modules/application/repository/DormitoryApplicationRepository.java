package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.DormitoryApplication;

import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY QUẢN LÝ HỒ SƠ ĐĂNG KÝ NỘI TRÚ
 * -----------------------------------------------------------------------
 * Sử dụng cơ chế "Query Derivation" (Sinh truy vấn tự động từ tên phương thức)
 * của Spring Data JPA dựa trên các nguyên tắc:
 *
 * 1. Chủ thể (Subject): findBy, existsBy, countBy...
 * 2. Thuộc tính (Predicate): Phải khớp chính xác với field trong Entity (CamelCase).
 * 3. Liên kết (Keywords): And, Or, Is, Between...
 * 4. Điều hướng (Navigation): Dấu gạch dưới "_" dùng để truy cập thuộc tính của Entity liên kết.
 * 5. Sắp xếp (Sorting): OrderBy + [Tên Field] + [Asc/Desc].
 */
@Repository
public interface DormitoryApplicationRepository
        extends JpaRepository<DormitoryApplication, Long> {

    Optional<DormitoryApplication>
    findByApplicationCode(String applicationCode);

    boolean existsByCccdAndRegistrationPeriod_Id(
            String cccd,
            Long registrationPeriodId
    );
    boolean existsByApplicationCode(
            String applicationCode
    );

    /**
     * TRUY VẤN PHÂN HẠNG (RANKING QUERY):
     * 1. Lọc theo trạng thái hồ sơ (Ví dụ: PENDING).
     * 2. Ưu tiên 1: PriorityScore giảm dần (Điểm cao xếp trước).
     * 3. Ưu tiên 2: SubmittedAt tăng dần (Nộp trước xếp trước nếu bằng điểm).
     */
    List<DormitoryApplication>
    findByStatusOrderByPriorityScoreDescSubmittedAtAsc(
            ApplicationStatus status
    );
}
