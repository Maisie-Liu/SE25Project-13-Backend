package com.campus.trading.service;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.entity.User;
import org.springframework.data.domain.Page;

public interface RecommendationService {
    /**
     * 为指定用户推荐物品
     * @param user 用户
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 推荐物品分页
     */
    Page<ItemDTO> recommendItems(User user, int pageNum, int pageSize);
} 