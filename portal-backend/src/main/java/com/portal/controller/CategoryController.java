package com.portal.controller;

import com.portal.dto.Result;
import com.portal.entity.ProjectCategory;
import com.portal.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目分类控制器
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<ProjectCategory>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public Result<ProjectCategory> getCategory(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @PostMapping
    public Result<ProjectCategory> createCategory(@RequestBody ProjectCategory category) {
        return Result.success("创建成功", categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public Result<ProjectCategory> updateCategory(@PathVariable Long id,
                                                   @RequestBody ProjectCategory category) {
        return Result.success("更新成功", categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
