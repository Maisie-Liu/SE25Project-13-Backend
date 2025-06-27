package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemCreateRequestDTO;
import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.ItemService;
import com.campus.trading.service.UserService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物品服务实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, ImageService imageService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public ItemDTO createItem(ItemCreateRequestDTO itemCreateRequest) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();

        // 创建物品实体
        Item item = new Item();
        // 设置物品信息
        // 注意：这里需要根据实际情况完善，如设置分类等
        
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
        // 注意：这里需要根据实际情况完善
        
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
        // 创建分页和排序参数
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sortObj);
        
        // 查询上架的物品
        Page<Item> itemPage = itemRepository.findByStatus(1, pageable);
        
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
        // 创建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询分类的物品
        Page<Item> itemPage = itemRepository.findByCategoryId(categoryId, pageable);
        
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
    public PageResponseDTO<ItemDTO> searchItems(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Integer condition, int pageNum, int pageSize, String sort, String order) {
        // 这里应该使用复杂查询，如Specification或自定义查询
        // 简单实现，仅使用关键字搜索
        
        // 创建分页和排序参数
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sortObj);
        
        Page<Item> itemPage;
        if (keyword != null && !keyword.isEmpty()) {
            itemPage = itemRepository.searchByKeyword(keyword, pageable);
        } else {
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
    public String generateItemDescription(String imageUrl) {
        // 实现根据图片生成描述的逻辑
        // 简单实现，返回一个假的描述
        return "这是一个自动生成的物品描述。";
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

    // 辅助方法：将实体转换为DTO
    private ItemDTO convertToDTO(Item item) {
        List<String> imageUrls = item.getImageIds() == null ? null : item.getImageIds().stream()
            .map(imageService::getImageUrl)
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
                .userAvatar(item.getUser() != null ? imageService.getImageUrl(item.getUser().getAvatarImageId()) : null)
                .createTime(item.getCreateTime())
                .updateTime(item.getUpdateTime())
                .build();
    }
} 