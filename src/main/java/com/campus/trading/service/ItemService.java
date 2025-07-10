package com.campus.trading.service;

import com.campus.trading.dto.ItemCreateRequestDTO;
import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 物品服务接口
 */
public interface ItemService {

    /**
     * 创建物品
     *
     * @param itemCreateRequest 物品创建请求
     * @return 创建的物品
     */
    ItemDTO createItem(ItemCreateRequestDTO itemCreateRequest);

    /**
     * 更新物品
     *
     * @param id               物品ID
     * @param itemCreateRequest 物品创建请求
     * @return 更新后的物品
     */
    ItemDTO updateItem(Long id, ItemCreateRequestDTO itemCreateRequest);

    /**
     * 获取物品详情
     *
     * @param id 物品ID
     * @return 物品详情
     */
    ItemDTO getItemById(Long id);

    /**
     * 删除物品
     *
     * @param id 物品ID
     * @return 是否删除成功
     */
    boolean deleteItem(Long id);

    /**
     * 上架物品
     *
     * @param id 物品ID
     * @return 上架后的物品
     */
    ItemDTO publishItem(Long id);

    /**
     * 下架物品
     *
     * @param id 物品ID
     * @return 下架后的物品
     */
    ItemDTO unpublishItem(Long id);

    /**
     * 分页查询物品列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param sort     排序方式（createTime, price, popularity）
     * @param order    排序顺序（asc, desc）
     * @return 物品分页列表
     */
    PageResponseDTO<ItemDTO> listItems(int pageNum, int pageSize, String sort, String order);

    /**
     * 分页查询用户物品列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 物品分页列表
     */
    PageResponseDTO<ItemDTO> listUserItems(Long userId, int pageNum, int pageSize);

    /**
     * 分页查询分类物品列表
     *
     * @param categoryId 分类ID
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 物品分页列表
     */
    PageResponseDTO<ItemDTO> listCategoryItems(Long categoryId, int pageNum, int pageSize);

    /**
     * 搜索物品
     *
     * @param keyword      关键词
     * @param categoryId   分类ID
     * @param minPrice     最低价格
     * @param maxPrice     最高价格
     * @param conditionMin 成色最小值
     * @param conditionMax 成色最大值
     * @param pageNum      页码
     * @param pageSize     每页大小
     * @param sort         排序方式
     * @param order        排序顺序
     * @return 物品分页列表
     */
    PageResponseDTO<ItemDTO> searchItems(String keyword, Long categoryId, BigDecimal minPrice, 
                                        BigDecimal maxPrice, Integer conditionMin, Integer conditionMax, 
                                        int pageNum, int pageSize, String sort, String order);

    /**
     * 根据图片生成物品描述
     *
     * @param imageUrl 图片URL
     * @return 物品描述
     */
    String generateItemDescription(String imageUrl) throws JsonProcessingException;

    /**
     * 获取推荐物品列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 推荐物品列表
     */
    List<ItemDTO> getRecommendedItems(int pageNum, int pageSize);

    /**
     * 增加物品热度
     *
     * @param id 物品ID
     */
    void incrementItemPopularity(Long id);

    /**
     * 获取平台统计数据
     *
     * @return 平台统计数据，包含商品总数、成交订单数和注册用户数
     */
    Map<String, Long> getPlatformStatistics();

    /**
     * 实体转DTO
     */
    ItemDTO convertToDTO(com.campus.trading.entity.Item item);
    
    /**
     * 获取用户所有物品
     *
     * @param userId 用户ID
     * @return 物品列表
     */
    List<Object> getItemsByUserId(Long userId);
} 