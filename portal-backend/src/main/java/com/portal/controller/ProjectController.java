package com.portal.controller;

import com.portal.dto.ProjectRequest;
import com.portal.dto.ProjectVO;
import com.portal.dto.Result;
import com.portal.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 项目控制器
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 获取所有项目
     */
    @GetMapping
    public Result<List<ProjectVO>> getAllProjects(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean featured) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            return Result.success(projectService.searchProjects(keyword.trim()));
        }
        if (featured != null && featured) {
            return Result.success(projectService.getFeaturedProjects());
        }
        if (categoryId != null) {
            return Result.success(projectService.getProjectsByCategory(categoryId));
        }
        return Result.success(projectService.getAllProjects());
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public Result<ProjectVO> getProject(@PathVariable Long id) {
        return Result.success(projectService.getProjectById(id));
    }

    /**
     * 跳转到项目（记录访问）
     */
    @PostMapping("/{id}/visit")
    public Result<String> visitProject(@PathVariable Long id) {
        String url = projectService.recordVisitAndGetUrl(id);
        return Result.success("跳转成功", url);
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(projectService.getStats());
    }

    // ===== 管理员接口 =====

    /**
     * 创建项目（需要管理员权限）
     */
    @PostMapping
    public Result<ProjectVO> createProject(@Valid @RequestBody ProjectRequest request) {
        return Result.success("创建成功", projectService.createProject(request));
    }

    /**
     * 更新项目（需要管理员权限）
     */
    @PutMapping("/{id}")
    public Result<ProjectVO> updateProject(@PathVariable Long id,
                                           @Valid @RequestBody ProjectRequest request) {
        return Result.success("更新成功", projectService.updateProject(id, request));
    }

    /**
     * 删除项目（需要管理员权限）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success("删除成功", null);
    }
}
