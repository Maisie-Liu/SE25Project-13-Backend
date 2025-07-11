package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.ItemCreateRequestDTO;
import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.User;
import com.campus.trading.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.campus.trading.service.UserService;
import com.campus.trading.service.UserViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * 物品控制器
 */
@RestController
@RequestMapping("/items")
@CrossOrigin
public class ItemController {

    private final ItemService itemService;
    private final UserViewService userViewService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserViewService userViewService, UserService userService) {
        this.itemService = itemService;
        this.userViewService = userViewService;
        this.userService = userService;
    }

    /**
     * 创建物品
     *
     * @param itemCreateRequest 物品创建请求
     * @return 创建的物品
     */
    @PostMapping
    public ApiResponse<ItemDTO> createItem(@RequestBody @Validated ItemCreateRequestDTO itemCreateRequest) {
        ItemDTO itemDTO = itemService.createItem(itemCreateRequest);
        return ApiResponse.success("创建物品成功", itemDTO);
    }

    /**
     * 更新物品
     *
     * @param id               物品ID
     * @param itemCreateRequest 物品创建请求
     * @return 更新后的物品
     */
    @PutMapping("/{id}")
    public ApiResponse<ItemDTO> updateItem(@PathVariable Long id, @RequestBody @Validated ItemCreateRequestDTO itemCreateRequest) {
        ItemDTO itemDTO = itemService.updateItem(id, itemCreateRequest);
        return ApiResponse.success("更新物品成功", itemDTO);
    }

    /**
     * 获取物品详情
     *
     * @param id 物品ID
     * @return 物品详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ItemDTO> getItemById(@PathVariable Long id) {
        // 先自增浏览量
        itemService.incrementItemPopularity(id);
        // 再获取物品详情（此时popularity为最新值）
        ItemDTO itemDTO = itemService.getItemById(id);
        // 再增加物品热度
        // itemService.incrementItemPopularity(id);
        // 自动记录浏览行为
        if (principal != null) {
            User user = userService.getCurrentUser(principal);
            if (user.isAllowPersonalizedRecommend()) {
                userViewService.recordView(user, itemService.getItemEntityById(id));
            }
        }
        return ApiResponse.success(itemDTO);
    }

    /**
     * 删除物品
     *
     * @param id 物品ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteItem(@PathVariable Long id) {
        boolean success = itemService.deleteItem(id);
        return ApiResponse.success("删除物品成功", success);
    }

    /**
     * 上架物品
     *
     * @param id 物品ID
     * @return 上架后的物品
     */
    @PutMapping("/{id}/publish")
    public ApiResponse<ItemDTO> publishItem(@PathVariable Long id) {
        ItemDTO itemDTO = itemService.publishItem(id);
        return ApiResponse.success("上架物品成功", itemDTO);
    }

    /**
     * 下架物品
     *
     * @param id 物品ID
     * @return 下架后的物品
     */
    @PutMapping("/{id}/unpublish")
    public ApiResponse<ItemDTO> unpublishItem(@PathVariable Long id) {
        ItemDTO itemDTO = itemService.unpublishItem(id);
        return ApiResponse.success("下架物品成功", itemDTO);
    }

    /**
     * 分页查询物品列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param sort     排序方式（createTime, price, popularity）
     * @param order    排序顺序（asc, desc）
     * @return 物品分页列表
     */
    @GetMapping
    public ApiResponse<PageResponseDTO<ItemDTO>> listItems(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String order) {
        PageResponseDTO<ItemDTO> pageResponse = itemService.listItems(pageNum, pageSize, sort, order);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 分页查询用户物品列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 物品分页列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResponseDTO<ItemDTO>> listUserItems(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponseDTO<ItemDTO> pageResponse = itemService.listUserItems(userId, pageNum, pageSize);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 分页查询分类物品列表
     *
     * @param categoryId 分类ID
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 物品分页列表
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<PageResponseDTO<ItemDTO>> listCategoryItems(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponseDTO<ItemDTO> pageResponse = itemService.listCategoryItems(categoryId, pageNum, pageSize);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 搜索物品
     *
     * @param keyword    关键字
     * @param categoryId 分类ID
     * @param minPrice   最低价格
     * @param maxPrice   最高价格
     * @param condition  新旧程度
     * @param conditionMin 新旧程度最小值
     * @param conditionMax 新旧程度最大值
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @param sort       排序方式
     * @param order      排序顺序
     * @return 物品分页列表
     */
    @GetMapping("/search")
    public ApiResponse<PageResponseDTO<ItemDTO>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Integer conditionMin,
            @RequestParam(required = false) Integer conditionMax,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String order) {
        // 兼容前端传condition为字符串的情况
        if (condition != null) {
            switch (condition.toLowerCase()) {
                case "all":
                    conditionMin = null;
                    conditionMax = null;
                    break;
                case "new":
                    conditionMin = 1;
                    conditionMax = 1;
                    break;
                case "like_new":
                    conditionMin = 2;
                    conditionMax = 3;
                    break;
                case "good":
                    conditionMin = 4;
                    conditionMax = 6;
                    break;
                case "acceptable":
                    conditionMin = 7;
                    conditionMax = 10;
                    break;
                default:
                    try {
                        int cond = Integer.parseInt(condition);
                        conditionMin = cond;
                        conditionMax = cond;
                    } catch (NumberFormatException ignored) {}
            }
        }
        PageResponseDTO<ItemDTO> pageResponse = itemService.searchItems(
                keyword, categoryId, minPrice, maxPrice, conditionMin, conditionMax, pageNum, pageSize, sort, order);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 根据图片生成物品描述
     *
     * @param imageId 图片ID
     * @return 物品描述
     */
    @PostMapping("/generate-description")
    public ApiResponse<String> generateItemDescription(@RequestParam String imageId) throws JsonProcessingException {
        String description = itemService.generateItemDescription(imageId);
        return ApiResponse.success("生成描述成功", description);
    }

    /**
     * 获取推荐物品列表（最热商品）
     * @return 推荐物品列表
     */
    @GetMapping("/recommended")
    public ApiResponse<List<ItemDTO>> getRecommendedItems() {
        List<ItemDTO> hotItems = itemService.getHotItems(5);
        return ApiResponse.success(hotItems);
    }

    /**
     * 获取平台统计数据
     *
     * @return 平台统计数据
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Long>> getPlatformStatistics() {
        Map<String, Long> statistics = itemService.getPlatformStatistics();
        return ApiResponse.success(statistics);
    }

    @PostMapping("/view/{itemId}")
    public ApiResponse<?> incrementPopularity(@PathVariable Long itemId) {
        itemService.incrementItemPopularity(itemId);
        return ApiResponse.success(null);
    }

    @GetMapping("/view/{itemId}")
    public ApiResponse<Long> getPopularity(@PathVariable Long itemId) {
        long popularity = itemService.getItemPopularity(itemId);
        return ApiResponse.success(popularity);
    }
} 