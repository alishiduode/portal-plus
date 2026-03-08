package com.portal.service;

import com.portal.entity.ProjectCategory;
import com.portal.repository.ProjectCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目分类服务
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProjectCategoryRepository categoryRepository;

    public List<ProjectCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAscNameAsc();
    }

    public ProjectCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在：" + id));
    }

    @Transactional
    public ProjectCategory createCategory(ProjectCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public ProjectCategory updateCategory(Long id, ProjectCategory updated) {
        ProjectCategory category = getCategoryById(id);
        category.setName(updated.getName());
        category.setIcon(updated.getIcon());
        category.setDescription(updated.getDescription());
        category.setSortOrder(updated.getSortOrder());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
