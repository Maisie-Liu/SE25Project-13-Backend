package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.RatingDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.dto.UserPublicProfileDTO;
import com.campus.trading.service.ItemService;
import com.campus.trading.service.RatingService;
import com.campus.trading.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final ItemService itemService;
    private final RatingService ratingService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, ItemService itemService, RatingService ratingService) {
        this.userService = userService;
        this.itemService = itemService;
        this.ratingService = ratingService;
    }

    /**
     * 获取用户公开资料
     *
     * @param userId 用户ID
     * @return 用户公开资料
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserPublicProfileDTO> getUserPublicProfile(@PathVariable Long userId) {
        try {
            logger.info("Getting public profile for user ID: {}", userId);
            
            // 获取用户基本信息
            logger.debug("尝试获取用户 ID: {} 的公开资料", userId);
            UserDTO userDTO = userService.getUserPublicProfile(userId);
            if (userDTO == null) {
                logger.warn("用户 ID: {} 不存在或查询失败", userId);
                return ApiResponse.notFound("用户不存在");
            }
            logger.debug("成功获取用户 ID: {} 的基本信息: {}", userId, userDTO);
            
            // 获取用户物品列表
            logger.debug("尝试获取用户 ID: {} 的物品列表", userId);
            List<Object> userItems = itemService.getItemsByUserId(userId);
            logger.debug("用户 ID: {} 物品数量: {}", userId, userItems.size());
            
            // 获取用户评分信息
            logger.debug("尝试获取用户 ID: {} 的评分信息", userId);
            List<RatingDTO> userRatings = ratingService.getUserRatings(userId);
            logger.debug("用户 ID: {} 评分数量: {}", userId, userRatings.size());
            
            // 组装用户公开资料
            UserPublicProfileDTO profileDTO = new UserPublicProfileDTO();
            profileDTO.setUser(userDTO);
            profileDTO.setItems(userItems);
            profileDTO.setRatings(userRatings);
            
            logger.info("成功获取并组装用户 ID: {} 的完整公开资料", userId);
            return ApiResponse.success(profileDTO);
        } catch (Exception e) {
            logger.error("Error getting user public profile for user ID: {}, error: {}", userId, e.getMessage(), e);
            return ApiResponse.error(500, "获取用户资料失败: " + e.getMessage());
        }
    }
    
    /**
     * 调试接口 - 检查用户访问权限
     */
    @GetMapping("/debug/access")
    public ApiResponse<Map<String, Object>> debugAccess() {
        logger.info("Debug access endpoint called");
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("status", "success");
            result.put("message", "Debug endpoint accessible without authentication");
            result.put("timestamp", System.currentTimeMillis());
            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("Error in debug access endpoint: {}", e.getMessage(), e);
            return ApiResponse.error(500, "调试接口错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口 - 获取所有用户公开ID列表
     */
    @GetMapping("/test/list")
    public ApiResponse<Map<String, Object>> testListUsers() {
        logger.info("Test list users endpoint called");
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("status", "success");
            result.put("message", "Test endpoint accessible");
            result.put("timestamp", System.currentTimeMillis());
            result.put("userCount", userService.getTotalUsers());
            result.put("userIds", "1,2,3,4,5");
            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("Error in test list users endpoint: {}", e.getMessage(), e);
            return ApiResponse.error(500, "测试接口错误: " + e.getMessage());
        }
    }
} 