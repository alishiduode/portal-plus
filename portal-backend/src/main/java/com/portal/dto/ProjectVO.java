package com.portal.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目详情VO（包含分类名称）
 */
@Data
public class ProjectVO {
    private Long id;
    private String name;
    private String description;
    private String url;
    private String icon;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private String techStack;
    private String status;
    private Boolean isFeatured;
    private Integer sortOrder;
    private Long visitCount;
    private String githubUrl;
    private String tags;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
