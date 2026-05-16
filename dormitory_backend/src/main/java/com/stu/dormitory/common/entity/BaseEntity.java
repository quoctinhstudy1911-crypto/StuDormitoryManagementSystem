package com.stu.dormitory.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * BaseEntity
 * - Mục tiêu:
 *      + Tạo ra là để làm lớp cha chung không phải để tạo ra đối tượng
 *      + Nếu có ai đó tạo nhầm đối tượng thì java sẽ báo lỗi ngay
 */
@MappedSuperclass
// JPA không tạo table riêng cho BaseEntity
// Các entity con sẽ kế thừa và map các field này xuống table của chính nó
@Getter @Setter // Dùng lombok tạo getter và setter tự động không cần sinh mã => Giảm dài dòng code ( này là ở compile )
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist // JPA Annotation chạy method tự động trước khi lưu object xuống database lần đầu
    protected void onCreate() {
      LocalDateTime now = LocalDateTime.now();
      this.createdAt = now;
      this.updatedAt = now;
    }

    // JPA Annotation mỗi lần update object thì sẽ cập nhật trước
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    // Kiến thức Entity Lifecycle Callback
}

/**
 * Kiến thức OOP
 * Này dùng kiến thức về tính trừu tượng Abstract: Chỉ giữ lại cái quan trọng ẩn đi cái chi tiết
 * Ở bài thì trừu tượng vấn đề mọi entity đều có id, createdAt và updateAt
 * Kiến thức về Encapsulation và Inheritance
 *
 * Kiến thức database
 * JPA hỗ trợ Entity Lifecycle Callback
 * Cơ chế này hoạt động tương tự trigger nhưng ở tầng application thay vì database
 */