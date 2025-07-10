package com.campus.trading.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 评分实体类
 */
@Entity
@Table(name = "ratings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 订单ID
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    /**
     * 物品ID
     */
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    /**
     * 用户ID (被评价者)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 评价者ID
     */
    @Column(name = "rater_id", nullable = false)
    private Long raterId;
    
    /**
     * 角色 (BUYER/SELLER)
     */
    @Column(nullable = false)
    private String role;
    
    /**
     * 评分 (1-5)
     */
    @Column(nullable = false)
    private Integer rating;
    
    /**
     * 评价内容
     */
    @Column(length = 500)
    private String comment;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
} 