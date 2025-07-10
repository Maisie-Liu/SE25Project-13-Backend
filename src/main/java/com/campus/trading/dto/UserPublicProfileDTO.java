package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户公开资料DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfileDTO {
    
    /**
     * 用户基本信息
     */
    private UserDTO user;
    
    /**
     * 用户物品列表
     */
    private List<Object> items;
    
    /**
     * 用户评价列表
     */
    private List<RatingDTO> ratings;
    
    /**
     * 统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsDTO {
        /**
         * 作为卖家的评分
         */
        private Double sellerRating;
        
        /**
         * 作为买家的评分
         */
        private Double buyerRating;
        
        /**
         * 卖家评分数量
         */
        private Integer sellerRatingCount;
        
        /**
         * 买家评分数量
         */
        private Integer buyerRatingCount;
        
        /**
         * 已售出物品数量
         */
        private Integer soldItemsCount;
        
        /**
         * 发布物品总数
         */
        private Integer totalItemsCount;
    }
} 