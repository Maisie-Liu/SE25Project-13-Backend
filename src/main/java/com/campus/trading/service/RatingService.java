package com.campus.trading.service;

import com.campus.trading.dto.RatingDTO;

import java.util.List;

/**
 * 评分服务接口
 */
public interface RatingService {

    /**
     * 获取用户评价列表
     *
     * @param userId 用户ID
     * @return 评价列表
     */
    List<RatingDTO> getUserRatings(Long userId);
    
    /**
     * 计算用户作为卖家的平均评分
     *
     * @param userId 用户ID
     * @return 平均评分
     */
    Double calculateSellerRating(Long userId);
    
    /**
     * 计算用户作为买家的平均评分
     *
     * @param userId 用户ID
     * @return 平均评分
     */
    Double calculateBuyerRating(Long userId);
} 