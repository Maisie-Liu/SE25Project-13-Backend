package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private LocalDateTime updateTime;
    // 类别ID -> 类别名
    private Map<Long, String> categoryIdToName;
    // 类别ID -> 兴趣权重
    private Map<Long, Double> categoryInterest;
} 