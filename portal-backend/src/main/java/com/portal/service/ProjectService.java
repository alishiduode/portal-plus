package com.portal.service;

import com.portal.dto.ProjectRequest;
import com.portal.dto.ProjectVO;
import com.portal.entity.Project;
import com.portal.entity.ProjectCategory;
import com.portal.repository.ProjectCategoryRepository;
import com.portal.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectCategoryRepository categoryRepository;

    /**
     * 获取所有项目（含分类名）
     */
    public List<ProjectVO> getAllProjects() {
        List<Project> projects = projectRepository.findAllByOrderBySortOrderAscCreateTimeDesc();
        Map<Long, String> categoryMap = getCategoryMap();
        return projects.stream()
                .map(p -> convertToVO(p, categoryMap))
                .collect(Collectors.toList());
    }

    /**
     * 根据分类获取项目
     */
    public List<ProjectVO> getProjectsByCategory(Long categoryId) {
        List<Project> projects = projectRepository.findByCategoryIdOrderBySortOrderAscCreateTimeDesc(categoryId);
        Map<Long, String> categoryMap = getCategoryMap();
        return projects.stream()
                .map(p -> convertToVO(p, categoryMap))
                .collect(Collectors.toList());
    }

    /**
     * 获取推荐项目
     */
    public List<ProjectVO> getFeaturedProjects() {
        List<Project> projects = projectRepository.findByIsFeaturedTrueOrderBySortOrderAscCreateTimeDesc();
        Map<Long, String> categoryMap = getCategoryMap();
        return projects.stream()
                .map(p -> convertToVO(p, categoryMap))
                .collect(Collectors.toList());
    }

    /**
     * 搜索项目
     */
    public List<ProjectVO> searchProjects(String keyword) {
        List<Project> projects = projectRepository.searchProjects(keyword);
        Map<Long, String> categoryMap = getCategoryMap();
        return projects.stream()
                .map(p -> convertToVO(p, categoryMap))
                .collect(Collectors.toList());
    }

    /**
     * 获取项目详情
     */
    public ProjectVO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("项目不存在：" + id));
        Map<Long, String> categoryMap = getCategoryMap();
        return convertToVO(project, categoryMap);
    }

    /**
     * 记录访问并返回跳转URL
     */
    @Transactional
    public String recordVisitAndGetUrl(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("项目不存在：" + id));
        projectRepository.incrementVisitCount(id);
        log.info("项目访问记录: id={}, name={}", id, project.getName());
        return project.getUrl();
    }

    /**
     * 创建项目
     */
    @Transactional
    public ProjectVO createProject(ProjectRequest request) {
        Project project = new Project();
        copyFromRequest(project, request);
        Project saved = projectRepository.save(project);
        Map<Long, String> categoryMap = getCategoryMap();
        return convertToVO(saved, categoryMap);
    }

    /**
     * 更新项目
     */
    @Transactional
    public ProjectVO updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("项目不存在：" + id));
        copyFromRequest(project, request);
        Project saved = projectRepository.save(project);
        Map<Long, String> categoryMap = getCategoryMap();
        return convertToVO(saved, categoryMap);
    }

    /**
     * 删除项目
     */
    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalProjects = projectRepository.count();
        long activeProjects = projectRepository.findByStatusOrderBySortOrderAscCreateTimeDesc(Project.ProjectStatus.ACTIVE).size();
        long totalVisits = projectRepository.findAll().stream()
                .mapToLong(p -> p.getVisitCount() != null ? p.getVisitCount() : 0)
                .sum();
        long totalCategories = categoryRepository.count();

        stats.put("totalProjects", totalProjects);
        stats.put("activeProjects", activeProjects);
        stats.put("totalVisits", totalVisits);
        stats.put("totalCategories", totalCategories);
        return stats;
    }

    // ===== 私有方法 =====

    private Map<Long, String> getCategoryMap() {
        return categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        ProjectCategory::getId,
                        ProjectCategory::getName
                ));
    }

    private ProjectVO convertToVO(Project project, Map<Long, String> categoryMap) {
        ProjectVO vo = new ProjectVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setDescription(project.getDescription());
        vo.setUrl(project.getUrl());
        vo.setIcon(project.getIcon());
        vo.setCoverImage(project.getCoverImage());
        vo.setCategoryId(project.getCategoryId());
        vo.setCategoryName(categoryMap.getOrDefault(project.getCategoryId(), "未分类"));
        vo.setTechStack(project.getTechStack());
        vo.setStatus(project.getStatus() != null ? project.getStatus().name() : "ACTIVE");
        vo.setIsFeatured(project.getIsFeatured());
        vo.setSortOrder(project.getSortOrder());
        vo.setVisitCount(project.getVisitCount());
        vo.setGithubUrl(project.getGithubUrl());
        vo.setTags(project.getTags());
        vo.setCreateTime(project.getCreateTime());
        vo.setUpdateTime(project.getUpdateTime());
        return vo;
    }

    private void copyFromRequest(Project project, ProjectRequest request) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setUrl(request.getUrl());
        project.setIcon(request.getIcon());
        project.setCoverImage(request.getCoverImage());
        project.setCategoryId(request.getCategoryId());
        project.setTechStack(request.getTechStack());
        project.setGithubUrl(request.getGithubUrl());
        project.setTags(request.getTags());
        if (request.getStatus() != null) {
            project.setStatus(Project.ProjectStatus.valueOf(request.getStatus()));
        }
        if (request.getIsFeatured() != null) {
            project.setIsFeatured(request.getIsFeatured());
        }
        if (request.getSortOrder() != null) {
            project.setSortOrder(request.getSortOrder());
        }
    }
}
