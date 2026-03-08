package com.portal.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 项目分类实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_category")
public class ProjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 分类名称
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 分类图标（Element Plus图标名称）
     */
    @Column(length = 100)
    private String icon;

    /**
     * 分类描述
     */
    @Column(length = 200)
    private String description;

    /**
     * 排序（数字越小越靠前）
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
