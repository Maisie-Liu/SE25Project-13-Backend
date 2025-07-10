package com.campus.trading.service;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.User;

import java.security.Principal;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     * @param itemId 物品ID
     * @return 收藏的物品信息
     */
    ItemDTO addFavorite(Long itemId);
    
    /**
     * 取消收藏
     * @param favoriteId 收藏ID
     * @return 是否成功
     */
    boolean removeFavorite(Long favoriteId);
    
    /**
     * 根据物品ID取消收藏
     * @param itemId 物品ID
     * @return 是否成功
     */
    boolean removeFavoriteByItemId(Long itemId);
    
    /**
     * 获取用户收藏列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 收藏的物品分页列表
     */
    PageResponseDTO<ItemDTO> getUserFavorites(int pageNum, int pageSize);
    
    /**
     * 检查用户是否已收藏某物品
     * @param itemId 物品ID
     * @return 收藏信息，如果未收藏则返回null
     */
    ItemDTO checkFavoriteStatus(Long itemId);

    User getCurrentUser(Principal principal);
}