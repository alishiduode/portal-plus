package com.portal.repository;

import com.portal.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 项目 Repository
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * 根据分类ID查询项目
     */
    List<Project> findByCategoryIdOrderBySortOrderAscCreateTimeDesc(Long categoryId);

    /**
     * 查询推荐项目
     */
    List<Project> findByIsFeaturedTrueOrderBySortOrderAscCreateTimeDesc();

    /**
     * 根据状态查询
     */
    List<Project> findByStatusOrderBySortOrderAscCreateTimeDesc(Project.ProjectStatus status);

    /**
     * 查询所有项目按排序
     */
    List<Project> findAllByOrderBySortOrderAscCreateTimeDesc();

    /**
     * 搜索项目（名称/描述/技术栈）
     */
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.techStack) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.sortOrder ASC, p.createTime DESC")
    List<Project> searchProjects(@Param("keyword") String keyword);

    /**
     * 增加访问量
     */
    @Modifying
    @Query("UPDATE Project p SET p.visitCount = p.visitCount + 1 WHERE p.id = :id")
    void incrementVisitCount(@Param("id") Long id);

    /**
     * 根据分类统计数量
     */
    @Query("SELECT p.categoryId, COUNT(p) FROM Project p GROUP BY p.categoryId")
    List<Object[]> countByCategoryId();
}
