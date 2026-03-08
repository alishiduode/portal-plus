package com.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 项目请求DTO
 */
@Data
public class ProjectRequest {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称不超过100字")
    private String name;

    @Size(max = 500, message = "项目描述不超过500字")
    private String description;

    @NotBlank(message = "访问URL不能为空")
    private String url;

    private String icon;
    private String coverImage;

    @NotNull(message = "请选择项目分类")
    private Long categoryId;

    private String techStack;
    private String status;
    private Boolean isFeatured;
    private Integer sortOrder;
    private String githubUrl;
    private String tags;
}
