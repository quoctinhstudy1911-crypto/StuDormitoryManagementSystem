package com.stu.dormitory.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * BaseEntity - Lớp cơ sở cho tất cả entities
 *
 * Cung cấp:
 * - id: Auto-increment primary key
 * - createdAt: Tự động set khi tạo (không thay đổi được)
 * - updatedAt: Tự động set khi update
 *
 * Sử dụng: Extends BaseEntity thay vì trực tiếp @Entity
 */
@MappedSuperclass
@Getter @Setter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
