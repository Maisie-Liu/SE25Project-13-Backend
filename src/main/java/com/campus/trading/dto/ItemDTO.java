package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private String description;
    private List<String> images;
    private Integer condition;
    private Integer status;
    private Integer popularity;
    private Long userId;
    private String username;
    private String userAvatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 