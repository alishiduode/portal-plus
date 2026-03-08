package com.portal.repository;

import com.portal.entity.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 项目分类 Repository
 */
@Repository
public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {

    /**
     * 查询所有分类按排序
     */
    List<ProjectCategory> findAllByOrderBySortOrderAscNameAsc();

    /**
     * 根据名称查询
     */
    ProjectCategory findByName(String name);
}
