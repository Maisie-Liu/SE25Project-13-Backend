package com.campus.trading.service.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationOutput;
import com.campus.trading.dto.ItemCreateRequestDTO;
import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.CategoryService;
import com.campus.trading.service.ItemService;
import com.campus.trading.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.campus.trading.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.campus.trading.config.Qwen3Properties;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;

/**
 * 物品服务实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);
    private final ImageService imageService;
    private final Qwen3Properties qwen3Properties;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, CategoryService categoryService, ImageService imageService, Qwen3Properties qwen3Properties) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.qwen3Properties = qwen3Properties;
    }

    @Override
    @Transactional
    public ItemDTO createItem(ItemCreateRequestDTO itemCreateRequest) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();

        // 创建物品实体
        Item item = Item.builder()
                .name(itemCreateRequest.getName())
                .category(categoryService.findById(itemCreateRequest.getCategoryId()))
                .price(itemCreateRequest.getPrice())
                .description(itemCreateRequest.getDescription())
                .imageIds(itemCreateRequest.getImages())
                .itemCondition(itemCreateRequest.getCondition())
                .status(1)
                .popularity(0)
                .user(currentUser)
                .stock(itemCreateRequest.getStock())
                .build();
        
        // 保存物品
        Item savedItem = itemRepository.save(item);
        
        // 转换为DTO返回
        return convertToDTO(savedItem);
    }

    @Override
    @Transactional
    public ItemDTO updateItem(Long id, ItemCreateRequestDTO itemCreateRequest) {
        // 获取物品
        Item item = getItemOrThrow(id);
        // 检查是否是物品所有者
        checkItemOwner(item);
        // 更新物品信息
        item.setName(itemCreateRequest.getName());
        item.setCategory(categoryService.findById(itemCreateRequest.getCategoryId()));
        item.setPrice(itemCreateRequest.getPrice());
        item.setDescription(itemCreateRequest.getDescription());
        // 更新图片ID
        List<String> imageIds = itemCreateRequest.getImages() != null ? new ArrayList<>(itemCreateRequest.getImages()) : new ArrayList<>();
        item.setImageIds(imageIds);
        item.setItemCondition(itemCreateRequest.getCondition());
        // 保存更新
        Item updatedItem = itemRepository.save(item);
        // 转换为DTO返回
        return convertToDTO(updatedItem);
    }

    @Override
    public ItemDTO getItemById(Long id) {
        Item item = getItemOrThrow(id);
        return convertToDTO(item);
    }

    @Override
    @Transactional
    public boolean deleteItem(Long id) {
        // 获取物品
        Item item = getItemOrThrow(id);
        
        // 检查是否是物品所有者
        checkItemOwner(item);
        
        // 删除物品
        itemRepository.delete(item);
        
        return true;
    }

    @Override
    @Transactional
    public ItemDTO publishItem(Long id) {
        // 获取物品
        Item item = getItemOrThrow(id);
        
        // 检查是否是物品所有者
        checkItemOwner(item);
        
        // 设置物品状态为上架
        item.setStatus(1);
        
        // 保存更新
        Item updatedItem = itemRepository.save(item);
        
        // 转换为DTO返回
        return convertToDTO(updatedItem);
    }

    @Override
    @Transactional
    public ItemDTO unpublishItem(Long id) {
        // 获取物品
        Item item = getItemOrThrow(id);
        
        // 检查是否是物品所有者
        checkItemOwner(item);
        
        // 设置物品状态为下架
        item.setStatus(0);
        
        // 保存更新
        Item updatedItem = itemRepository.save(item);
        
        // 转换为DTO返回
        return convertToDTO(updatedItem);
    }

    @Override
    public PageResponseDTO<ItemDTO> listItems(int pageNum, int pageSize, String sort, String order) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Item> itemPage = itemRepository.findByStatusAndStockGreaterThan(1, 0, pageable);
        List<ItemDTO> itemDTOs = itemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageResponseDTO<>(
                itemDTOs,
                itemPage.getTotalElements(),
                pageNum,
                pageSize,
                itemPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<ItemDTO> listUserItems(Long userId, int pageNum, int pageSize) {
        // 获取用户
        User user = userService.findById(userId);
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询用户的物品
        Page<Item> itemPage = itemRepository.findByUser(user, pageable);
        
        // 转换为DTO
        List<ItemDTO> itemDTOs = itemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        return new PageResponseDTO<>(
                itemDTOs,
                itemPage.getTotalElements(),
                pageNum,
                pageSize,
                itemPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<ItemDTO> listCategoryItems(Long categoryId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Item> itemPage = itemRepository.findByCategoryIdAndStatusAndStockGreaterThan(categoryId, 1, 0, pageable);
        List<ItemDTO> itemDTOs = itemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageResponseDTO<>(
                itemDTOs,
                itemPage.getTotalElements(),
                pageNum,
                pageSize,
                itemPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<ItemDTO> searchItems(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Integer condition, int pageNum, int pageSize, String sort, String order) {
        // 创建分页和排序参数
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj;
        
        // 根据排序字段创建排序对象
        switch (sort.toLowerCase()) {
            case "price":
                sortObj = Sort.by(direction, "price");
                break;
            case "popularity":
                sortObj = Sort.by(Sort.Direction.DESC, "popularity");
                break;
            case "createtime":
                sortObj = Sort.by(direction, "createTime");
                break;
            default:
                sortObj = Sort.by(Sort.Direction.DESC, "createTime");
        }
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sortObj);
        Page<Item> itemPage;

        // 如果有关键字，使用关键字搜索
        if (keyword != null && !keyword.isEmpty()) {
            itemPage = itemRepository.searchByKeyword(keyword, pageable);
        } 
        // 如果指定了按热度排序，使用专门的查询方法
        else if ("popularity".equalsIgnoreCase(sort)) {
            itemPage = itemRepository.findByStatusOrderByPopularityDesc(1, pageable);
        }
        // 否则使用普通查询
        else {
            itemPage = itemRepository.findByStatus(1, pageable);
        }
        
        // 转换为DTO
        List<ItemDTO> itemDTOs = itemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        return new PageResponseDTO<>(
                itemDTOs,
                itemPage.getTotalElements(),
                pageNum,
                pageSize,
                itemPage.getTotalPages()
        );
    }

    @Override
    public String generateItemDescription(String imageId) throws JsonProcessingException {
        // 1. 获取公网可访问的图片URL
        String imageUrl = imageService.generateAIImageAccessToken(imageId);
        log.info("Qwen3 image url: {}", imageUrl);
        // 2. 直接调用Qwen3进行识别和文案生成
        return callQwen3ForImageDescription(imageUrl);
    }

    @Override
    public List<ItemDTO> getRecommendedItems(int pageNum, int pageSize) {
        // 创建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询热门物品
        Page<Item> itemPage = itemRepository.findAllByOrderByPopularityDesc(pageable);
        
        // 转换为DTO
        return itemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementItemPopularity(Long id) {
        // 获取物品
        Item item = getItemOrThrow(id);
        
        // 增加热度
        item.setPopularity(item.getPopularity() + 1);
        
        // 保存更新
        itemRepository.save(item);
    }

    @Override
    public Map<String, Long> getPlatformStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        
        // 获取上架商品总数
        long totalItems = itemRepository.countByStatus(1);
        statistics.put("totalItems", totalItems);
        
        // 获取成交订单总数（状态为3表示已售出）
        long completedOrders = itemRepository.countByStatus(3);
        statistics.put("completedOrders", completedOrders);
        
        // 获取注册用户总数
        long totalUsers = userService.getTotalUsers();
        statistics.put("totalUsers", totalUsers);
        
        // 添加调试日志
        log.info("Platform statistics: {}", statistics);
        
        return statistics;
    }

    @Override
    public ItemDTO convertToDTO(Item item) {
        List<String> imageIds = item.getImageIds();
        List<String> imageUrls = imageIds == null ? null : imageIds.stream()
            .map(imageService::generateImageAccessToken)
            .collect(java.util.stream.Collectors.toList());
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .price(item.getPrice())
                .description(item.getDescription())
                .imageUrls(imageUrls)
                .condition(item.getItemCondition())
                .status(item.getStatus())
                .popularity(item.getPopularity())
                .userId(item.getUser() != null ? item.getUser().getId() : null)
                .username(item.getUser() != null ? item.getUser().getUsername() : null)
                .userAvatar(item.getUser() != null ? imageService.generateImageAccessToken(item.getUser().getAvatarImageId()) : null)
                .createTime(item.getCreateTime())
                .updateTime(item.getUpdateTime())
                .stock(item.getStock())
                .build();
    }

    // 辅助方法：获取物品或抛出异常
    private Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("物品不存在: ID=" + id));
    }

    // 辅助方法：检查是否是物品所有者
    private void checkItemOwner(Item item) {
        User currentUser = getCurrentUser();
        if (!item.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只有物品所有者才能执行此操作");
        }
    }

    // 辅助方法：获取当前登录用户
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username);
    }

    // 辅助方法：调用Qwen3生成商品描述
    private String callQwen3ForImageDescription(String imageUrl) {
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap("image", imageUrl),
                            Collections.singletonMap("text", "请识别图片中的商品，并仿照闲鱼发帖风格，生成一段简洁吸引人的、发布于于大学校园二手物品交易平台的商品描述，避免使用markdown进行格式渲染。")
                    ))
                    .build();
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(qwen3Properties.getApiKey())
                    .model("qwen-vl-max")
                    .message(userMessage)
                    .build();
            MultiModalConversationResult result = conv.call(param);
            if (result != null && result.getOutput() != null
                && result.getOutput().getChoices() != null
                && !result.getOutput().getChoices().isEmpty()) {
            MultiModalConversationOutput.Choice choice = result.getOutput().getChoices().get(0);
            if (choice != null && choice.getMessage() != null
                    && choice.getMessage().getContent() != null
                    && !choice.getMessage().getContent().isEmpty()) {
                Object textObj = choice.getMessage().getContent().get(0).get("text");
                if (textObj != null) {
                    return textObj.toString();
                }
            }
        }
            log.info("Qwen3生成描述失败, result: {}", result);
            return "AI生成描述失败";
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            log.error("调用Qwen3生成商品描述失败", e);
            return "AI生成描述失败";
        }
    }
} 