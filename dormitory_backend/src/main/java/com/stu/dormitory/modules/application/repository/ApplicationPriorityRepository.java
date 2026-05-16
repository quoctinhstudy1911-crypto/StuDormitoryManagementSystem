package com.stu.dormitory.modules.application.repository;

import com.stu.dormitory.modules.application.entity.ApplicationPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationPriorityRepository extends JpaRepository<ApplicationPriority, Long> {

    /**
     * Tìm danh sách các quyền ưu tiên theo ID của đơn đăng ký ký túc xá.
     *
     * KIẾN THỨC SPRING DATA JPA - QUERY CREATION MECHANISM:
     * - Tên hàm tuân theo quy tắc CamelCase: findBy + [TênBiếnEntityLiênKết] + [TênThuộcTínhCủaEntityĐó].
     * - Trong class ApplicationPriority, bạn đặt tên biến liên kết là 'application' (kiểu DormitoryApplication).
     * - Trong class DormitoryApplication (kế thừa từ BaseEntity), thuộc tính khóa chính tên là 'id'.
     * -> Ghép lại thành: findBy + Application + Id = findByApplicationId.
     *
     * TẠI SAO KHÔNG NÊN DÙNG DẤU GẠCH DƯỚI (_)?
     * - Nếu viết findByApplication_Id (chữ A viết hoa), Spring Boot sẽ tìm thuộc tính tên là 'Application'
     *   ở thực thể gốc. Vì Java phân biệt hoa-thường (application != Application), ứng dụng sẽ bị crash
     *   ngay khi khởi động (PropertyReferenceException).
     * - Viết liền không dấu '_' giúp code sạch, đúng chuẩn Java Convention và an toàn tuyệt đối.
     *
     * @param applicationId ID của DormitoryApplication (Đơn đăng ký)
     * @return Danh sách các ApplicationPriority thuộc về đơn đăng ký đó
     */
    List<ApplicationPriority> findByApplication_Id(Long applicationId);
}