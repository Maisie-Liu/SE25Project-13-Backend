package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.service.FavoriteService;
import com.campus.trading.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/favorites")
@CrossOrigin
public class FavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);
    private final FavoriteService favoriteService;
    private final UserProfileService userProfileService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService, UserProfileService userProfileService) {
        this.favoriteService = favoriteService;
        this.userProfileService = userProfileService;
    }

    /**
     * 获取用户收藏列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 收藏列表
     */
    @GetMapping
    public ApiResponse<PageResponseDTO<ItemDTO>> getUserFavorites(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        logger.info("获取用户收藏列表请求: pageNum={}, pageSize={}", pageNum, pageSize);
        try {
            PageResponseDTO<ItemDTO> favorites = favoriteService.getUserFavorites(pageNum, pageSize);
            logger.info("获取用户收藏列表成功: 总数={}", favorites.getTotal());
            return ApiResponse.success("获取收藏列表成功", favorites);
        } catch (Exception e) {
            logger.error("获取用户收藏列表失败", e);
            return ApiResponse.error(500, "获取收藏列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加收藏
     *
     * @param request 包含物品ID的请求
     * @return 收藏结果
     */
    @PostMapping
    public ApiResponse<ItemDTO> addFavorite(@RequestBody Map<String, Long> request, Principal principal) {
        Long itemId = request.get("itemId");
        logger.info("添加收藏请求: itemId={}", itemId);
        if (itemId == null) {
            return ApiResponse.error(400, "物品ID不能为空");
        }
        try {
            ItemDTO item = favoriteService.addFavorite(itemId);
            // 自动更新用户画像
            if (principal != null) {
                userProfileService.updateProfile(favoriteService.getCurrentUser(principal));
            }
            logger.info("添加收藏成功: itemId={}", itemId);
            return ApiResponse.success("收藏成功", item);
        } catch (Exception e) {
            logger.error("添加收藏失败: itemId={}", itemId, e);
            return ApiResponse.error(500, "添加收藏失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏
     *
     * @param id 收藏ID
     * @return 取消结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> removeFavorite(@PathVariable Long id, Principal principal) {
        logger.info("取消收藏请求: id={}", id);
        try {
            boolean success = favoriteService.removeFavorite(id);
            // 自动更新用户画像
            if (principal != null) {
                userProfileService.updateProfile(favoriteService.getCurrentUser(principal));
            }
            logger.info("取消收藏成功: id={}", id);
            return ApiResponse.success("取消收藏成功", success);
        } catch (Exception e) {
            logger.error("取消收藏失败: id={}", id, e);
            return ApiResponse.error(500, "取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 根据物品ID取消收藏
     *
     * @param itemId 物品ID
     * @return 取消结果
     */
    @DeleteMapping("/item/{itemId}")
    public ApiResponse<Boolean> removeFavoriteByItemId(@PathVariable Long itemId, Principal principal) {
        logger.info("根据物品ID取消收藏请求: itemId={}", itemId);
        try {
            boolean success = favoriteService.removeFavoriteByItemId(itemId);
            // 自动更新用户画像
            if (principal != null) {
                userProfileService.updateProfile(favoriteService.getCurrentUser(principal));
            }
            logger.info("根据物品ID取消收藏成功: itemId={}", itemId);
            return ApiResponse.success("取消收藏成功", success);
        } catch (Exception e) {
            logger.error("根据物品ID取消收藏失败: itemId={}, 错误信息: {}", itemId, e.getMessage(), e);
            // 如果是EntityNotFoundException，返回404
            if (e instanceof javax.persistence.EntityNotFoundException) {
                return ApiResponse.error(404, "收藏不存在: " + e.getMessage());
            }
            return ApiResponse.error(500, "取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 检查物品是否已收藏
     *
     * @param itemId 物品ID
     * @return 收藏信息，如果未收藏则返回null
     */
    @GetMapping("/check/{itemId}")
    public ApiResponse<ItemDTO> checkFavoriteStatus(@PathVariable Long itemId) {
        logger.info("检查物品收藏状态请求: itemId={}", itemId);
        try {
            ItemDTO favoriteItem = favoriteService.checkFavoriteStatus(itemId);
            if (favoriteItem != null) {
                logger.info("检查物品收藏状态成功: itemId={}, isFavorite=true", itemId);
                return ApiResponse.success("已收藏", favoriteItem);
            } else {
                logger.info("检查物品收藏状态成功: itemId={}, isFavorite=false", itemId);
                return ApiResponse.success("未收藏", null);
            }
        } catch (Exception e) {
            logger.error("检查物品收藏状态失败: itemId={}", itemId, e);
            return ApiResponse.error(500, "检查收藏状态失败: " + e.getMessage());
        }
    }
} 