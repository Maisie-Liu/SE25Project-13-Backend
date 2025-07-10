package com.campus.trading.repository;

import com.campus.trading.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评分仓库
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    /**
     * 通过用户ID查找评价
     *
     * @param userId 用户ID
     * @return 评价列表
     */
    List<Rating> findByUserId(Long userId);
    
    /**
     * 通过用户ID和角色查找评价
     *
     * @param userId 用户ID
     * @param role 角色
     * @return 评价列表
     */
    List<Rating> findByUserIdAndRole(Long userId, String role);
    
    /**
     * 计算用户作为卖家的平均评分
     *
     * @param userId 用户ID
     * @return 平均评分
     */
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.userId = :userId AND r.role = 'SELLER'")
    Double calculateSellerRating(@Param("userId") Long userId);
    
    /**
     * 计算用户作为买家的平均评分
     *
     * @param userId 用户ID
     * @return 平均评分
     */
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.userId = :userId AND r.role = 'BUYER'")
    Double calculateBuyerRating(@Param("userId") Long userId);
} 