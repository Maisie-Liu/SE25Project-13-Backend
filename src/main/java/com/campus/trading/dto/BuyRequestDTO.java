package com.campus.trading.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BuyRequestDTO {
    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private Integer condition;
    private BigDecimal expectedPrice;
    private Boolean negotiable;
    private String description;
    private String contact;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long userId;
    private String username;
    private String userAvatar;
    private Integer commentCount;
} 