package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评分DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    
    /**
     * 评分ID
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 物品ID
     */
    private Long itemId;
    
    /**
     * 物品信息
     */
    private ItemDTO item;
    
    /**
     * 用户ID (被评价者)
     */
    private Long userId;
    
    /**
     * 评价者ID
     */
    private Long raterId;
    
    /**
     * 评价者信息
     */
    private UserDTO rater;
    
    /**
     * 角色 (BUYER/SELLER)
     */
    private String role;
    
    /**
     * 评分 (1-5)
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 