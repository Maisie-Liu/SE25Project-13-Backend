package com.campus.trading.service.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationOutput;
import com.campus.trading.dto.ItemCreateRequestDTO;
import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.entity.Category;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.CategoryRepository;
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
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.campus.trading.entity.ItemDocument;
import com.campus.trading.repository.ItemESRepository;
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
    private final CategoryRepository categoryRepository;
    private final ItemESRepository itemESRepository;
    private final Qwen3Properties qwen3Properties;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, CategoryService categoryService, ImageService imageService, CategoryRepository categoryRepository, ItemESRepository itemESRepository, Qwen3Properties qwen3Properties) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.categoryRepository = categoryRepository;
        this.itemESRepository = itemESRepository;
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
        item.setStock(itemCreateRequest.getStock());
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
        // 删除功能已移除，仅允许下架
        throw new UnsupportedOperationException("删除物品功能已禁用，请使用下架操作");
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
    public PageResponseDTO<ItemDTO> searchItems(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Integer conditionMin, Integer conditionMax, int pageNum, int pageSize, String sort, String order) {
        // 只有keyword不为空时才用ES，否则直接用数据库
        if (keyword != null && !keyword.isEmpty()) {
            List<ItemDocument> docs = itemESRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
            // 过滤其它条件
            List<ItemDocument> filtered = docs.stream()
                .filter(doc -> (categoryId == null || (doc.getCategoryId() != null && doc.getCategoryId().equals(categoryId)))
                    && (minPrice == null || (doc.getPrice() != null && doc.getPrice().compareTo(minPrice) >= 0))
                    && (maxPrice == null || (doc.getPrice() != null && doc.getPrice().compareTo(maxPrice) <= 0))
                    && (conditionMin == null || (doc.getItemCondition() != null && doc.getItemCondition() >= conditionMin))
                    && (conditionMax == null || (doc.getItemCondition() != null && doc.getItemCondition() <= conditionMax))
                    && (doc.getStatus() != null && doc.getStatus() == 1)
                    && (doc.getStock() != null && doc.getStock() > 0)
                )
                .collect(Collectors.toList());
            // 排序
            filtered = filtered.stream().sorted((a, b) -> {
                int cmp = 0;
                switch (sort.toLowerCase()) {
                    case "price":
                        cmp = a.getPrice().compareTo(b.getPrice());
                        break;
                    case "popularity":
                    case "views":
                        cmp = b.getPopularity() - a.getPopularity();
                        break;
                    case "createtime":
                        cmp = b.getCreateTime().compareTo(a.getCreateTime());
                        break;
                    default:
                        cmp = b.getCreateTime().compareTo(a.getCreateTime());
                }
                return "asc".equalsIgnoreCase(order) ? -cmp : cmp;
            }).collect(Collectors.toList());
            // 分页
            int total = filtered.size();
            int from = Math.max(0, (pageNum - 1) * pageSize);
            int to = Math.min(filtered.size(), from + pageSize);
            List<ItemDTO> itemDTOs = filtered.subList(from, to).stream().map(this::convertESDocToDTO).collect(Collectors.toList());
            return new PageResponseDTO<>(itemDTOs, total, pageNum, pageSize, (total + pageSize - 1) / pageSize);
        }
        // keyword为空直接用数据库SQL
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj;
        switch (sort.toLowerCase()) {
            case "price":
                sortObj = Sort.by(direction, "price");
                break;
            case "popularity":
                sortObj = Sort.by(Sort.Direction.DESC, "popularity");
                break;
            case "views":
                sortObj = Sort.by(direction, "popularity");
                break;
            case "createtime":
                sortObj = Sort.by(direction, "createTime");
                break;
            default:
                sortObj = Sort.by(Sort.Direction.DESC, "createTime");
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sortObj);
        Specification<Item> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.equal(root.get("status"), 1));
            p = cb.and(p, cb.greaterThan(root.get("stock"), 0));
            if (keyword != null && !keyword.isEmpty()) {
                Predicate nameLike = cb.like(root.get("name"), "%" + keyword + "%");
                Predicate descLike = cb.like(root.get("description"), "%" + keyword + "%");
                p = cb.and(p, cb.or(nameLike, descLike));
            }
            if (categoryId != null) {
                List<Long> categoryIds = new ArrayList<>();
                categoryIds.add(categoryId);
                List<Category> subCategories = categoryRepository.findByParentId(categoryId);
                for (Category sub : subCategories) {
                    categoryIds.add(sub.getId());
                }
                p = cb.and(p, root.get("category").get("id").in(categoryIds));
            }
            if (minPrice != null) {
                p = cb.and(p, cb.ge(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                p = cb.and(p, cb.le(root.get("price"), maxPrice));
            }
            if (conditionMin != null && conditionMax != null) {
                p = cb.and(p, cb.between(root.get("itemCondition"), conditionMin, conditionMax));
            } else if (conditionMin != null) {
                p = cb.and(p, cb.ge(root.get("itemCondition"), conditionMin));
            } else if (conditionMax != null) {
                p = cb.and(p, cb.le(root.get("itemCondition"), conditionMax));
            }
            return p;
        };
        Page<Item> itemPage = itemRepository.findAll(spec, pageable);
        List<ItemDTO> itemDTOs = itemPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResponseDTO<>(itemDTOs, itemPage.getTotalElements(), pageNum, pageSize, itemPage.getTotalPages());
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
        statistics.put("totalItems", itemRepository.count());
        statistics.put("totalUsers", userService.getTotalUsers());
        statistics.put("totalListedItems", itemRepository.countByStatus(1));
        return statistics;
    }

    @Override
    public ItemDTO convertToDTO(Item item) {
        if (item == null) {
            return null;
        }
        
        // 处理图片URLs
        List<String> imageUrls = null;
        if (item.getImageIds() != null) {
            imageUrls = item.getImageIds().stream()
                .map(imageService::generateImageAccessToken)
                .collect(Collectors.toList());
        }
        
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .description(item.getDescription())
                .imageUrls(imageUrls)
                .imageIds(item.getImageIds())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .condition(item.getItemCondition())
                .status(item.getStatus())
                .userId(item.getUser().getId())
                .username(item.getUser().getUsername())
                .userAvatar(item.getUser().getAvatarImageId() != null ? imageService.generateImageAccessToken(item.getUser().getAvatarImageId()) : null)
                .createTime(item.getCreateTime())
                .popularity(item.getPopularity())
                .stock(item.getStock())
                .build();
    }
    
    @Override
    public List<Object> getItemsByUserId(Long userId) {
        // 获取用户
        User user = userService.findById(userId);
        
        // 查询用户的所有物品（按创建时间倒序）
        List<Item> items = itemRepository.findByUserOrderByCreateTimeDesc(user);
        
        // 转换为DTO并返回
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 新增：ES实体转DTO
    private ItemDTO convertESDocToDTO(ItemDocument doc) {
        return ItemDTO.builder()
                .id(doc.getId())
                .name(doc.getName())
                .categoryId(doc.getCategoryId())
                .categoryName(doc.getCategoryName())
                .price(doc.getPrice())
                .description(doc.getDescription())
                .imageUrls(doc.getImageIds() == null ? null : doc.getImageIds().stream().map(imageService::generateImageAccessToken).collect(Collectors.toList()))
                .condition(doc.getItemCondition())
                .status(doc.getStatus())
                .popularity(doc.getPopularity())
                .userId(doc.getUserId())
                .username(doc.getUsername())
                .userAvatar(doc.getUserAvatar() != null ? imageService.generateImageAccessToken(doc.getUserAvatar()) : null)
                .createTime(doc.getCreateTime())
                .updateTime(doc.getUpdateTime())
                .stock(doc.getStock())
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

    /**
     * 批量同步所有商品到ElasticSearch
     */
    @Transactional
    public void syncAllItemsToES() {
        List<Item> allItems = itemRepository.findAll();
        List<ItemDocument> docs = new ArrayList<>();
        for (Item item : allItems) {
            // 强制初始化imageIds，防止懒加载异常
            if (item.getImageIds() != null) {
                item.getImageIds().size();
            }
            ItemDocument doc = new ItemDocument();
            doc.setId(item.getId());
            doc.setName(item.getName());
            doc.setCategoryId(item.getCategory() != null ? item.getCategory().getId() : null);
            doc.setCategoryName(item.getCategory() != null ? item.getCategory().getName() : null);
            doc.setPrice(item.getPrice());
            doc.setDescription(item.getDescription());
            doc.setImageIds(item.getImageIds());
            doc.setItemCondition(item.getItemCondition());
            doc.setStatus(item.getStatus());
            doc.setPopularity(item.getPopularity());
            doc.setUserId(item.getUser() != null ? item.getUser().getId() : null);
            doc.setUsername(item.getUser() != null ? item.getUser().getUsername() : null);
            doc.setUserAvatar(item.getUser() != null ? item.getUser().getAvatarImageId() : null);
            doc.setCreateTime(item.getCreateTime());
            doc.setUpdateTime(item.getUpdateTime());
            doc.setStock(item.getStock());
            docs.add(doc);
        }
        itemESRepository.saveAll(docs);
        System.out.println("已同步商品数据到ElasticSearch，数量：" + docs.size());
    }

    // 辅助方法：调用Qwen3生成商品描述
    private String callQwen3ForImageDescription(String imageUrl) {
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap("image", imageUrl),
                            Collections.singletonMap("text", "请识别图片中的商品，并仿照闲鱼发帖风格，生成一段简洁吸引人的、发布于于大学校园二手物品交易平台的商品描述，避免使用markdown进行格式渲染，使用utf-8字符集纯文字，避免使用emoji。")
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

    @Override
    public Item getItemEntityById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("物品不存在: ID=" + id));
    }
} 