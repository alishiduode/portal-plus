package com.portal.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 项目实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 项目名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 项目描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 访问URL
     */
    @Column(nullable = false, length = 500)
    private String url;

    /**
     * 项目图标URL
     */
    @Column(length = 500)
    private String icon;

    /**
     * 项目封面图
     */
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    /**
     * 所属分类ID
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 技术栈描述
     */
    @Column(name = "tech_stack", length = 200)
    private String techStack;

    /**
     * 项目状态：ACTIVE-运行中, MAINTENANCE-维护中, OFFLINE-已下线
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    /**
     * 是否推荐（首页展示）
     */
    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    /**
     * 排序（数字越小越靠前）
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 访问次数统计
     */
    @Column(name = "visit_count")
    private Long visitCount = 0L;

    /**
     * GitHub地址
     */
    @Column(name = "github_url", length = 500)
    private String githubUrl;

    /**
     * 标签（逗号分隔）
     */
    @Column(length = 200)
    private String tags;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 项目状态枚举
     */
    public enum ProjectStatus {
        ACTIVE,       // 运行中
        MAINTENANCE,  // 维护中
        OFFLINE       // 已下线
    }
}
