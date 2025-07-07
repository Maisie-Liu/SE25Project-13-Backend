package com.campus.trading.service.impl;

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
import org.springframework.web.multipart.MultipartFile;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tiia.v20190529.TiiaClient;
import com.tencentcloudapi.tiia.v20190529.models.DetectProductRequest;
import com.tencentcloudapi.tiia.v20190529.models.DetectProductResponse;
import com.campus.trading.config.TencentCloudProperties;
import com.campus.trading.config.DeepSeekProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final TencentCloudProperties tencentCloudProperties;
    private final DeepSeekProperties deepSeekProperties;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, CategoryService categoryService, ImageService imageService, TencentCloudProperties tencentCloudProperties, DeepSeekProperties deepSeekProperties) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.tencentCloudProperties = tencentCloudProperties;
        this.deepSeekProperties = deepSeekProperties;
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
    public String uploadItemImage(MultipartFile file) {
        // 实现文件上传逻辑
        // 简单实现，返回一个假的URL
        return "http://example.com/images/" + System.currentTimeMillis() + ".jpg";
    }

    @Override
    public String generateItemDescription(String imageId) throws JsonProcessingException {
        // 1. 通过图片ID获取带token的公网图片URL（AI专用）
        String imageUrl = imageService.generateAIImageAccessToken(imageId);
        log.info(imageUrl);
        // 2. 调用腾讯云商品识别
        String detectResultJson = detectProductByImageUrl(imageUrl);
        // 3. 解析商品名称和类别，取置信度最高的商品
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = mapper.readValue(detectResultJson, Map.class);
        Object responseObj = respMap.get("Response");
        if (responseObj == null) {
            log.error("腾讯云商品识别API返回异常: {}", detectResultJson);
            return "AI识别失败，返回内容异常";
        }
        Map<String, Object> response = (Map<String, Object>) responseObj;
        List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("Products");
        if (products == null || products.isEmpty()) {
            return "未识别到商品，无法生成描述。";
        }
        // 选出置信度最高的商品
        Map<String, Object> bestProduct = products.get(0);
        for (Map<String, Object> prod : products) {
            if (((Number)prod.get("Confidence")).intValue() > ((Number)bestProduct.get("Confidence")).intValue()) {
                bestProduct = prod;
            }
        }
        String name = (String) bestProduct.get("Name");
        String category = (String) bestProduct.get("Parents");
        // 4. 调用DeepSeek生成文案
        return callDeepSeekForDescription(name, category);
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

    // 商品识别
    public String detectProductByImageUrl(String imageUrl) {
        try {
            Credential cred = new Credential(
                tencentCloudProperties.getSecretId(),
                tencentCloudProperties.getSecretKey()
            );
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tiia.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            TiiaClient client = new TiiaClient(cred, tencentCloudProperties.getRegion(), clientProfile);

            DetectProductRequest req = new DetectProductRequest();
            req.setImageUrl(imageUrl);

            DetectProductResponse resp = client.DetectProduct(req);

            return DetectProductResponse.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            log.error("调用腾讯云商品识别失败", e);
            throw new RuntimeException("商品识别失败: " + e.getMessage());
        }
    }

    private String callDeepSeekForDescription(String productName, String productCategory) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = deepSeekProperties.getBaseUrl() + "/chat/completions";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepSeekProperties.getApiKey());

            // 构造 prompt
            String prompt = String.format("请为商品\"%s\"（类别：%s）生成一段简洁吸引人的二手商品描述，突出产品名称、应用场景、物品状态等。", productName, productCategory);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "你是一个优秀的生成二手商品描述的文案助手。"));
            messages.add(Map.of("role", "user", "content", prompt));
            body.put("messages", messages);
            body.put("stream", false);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> respMap = mapper.readValue(response.getBody(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "AI生成描述失败";
        } catch (Exception e) {
            log.error("调用DeepSeek生成商品描述失败", e);
            return "AI生成描述失败";
        }
    }
} 