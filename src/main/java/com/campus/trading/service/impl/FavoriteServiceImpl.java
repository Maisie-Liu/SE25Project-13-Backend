package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.FavoriteMessage;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.FavoriteRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.FavoriteService;
import com.campus.trading.service.MessageService;
import com.campus.trading.service.ItemService;
import com.campus.trading.service.UserService;
import com.campus.trading.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);
    private final FavoriteRepository favoriteRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final MessageService messageService;
    private final ItemService itemService;
    private final UserProfileService userProfileService;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, ItemRepository itemRepository, 
                              UserService userService, MessageService messageService, ItemService itemService, UserProfileService userProfileService) {
        this.favoriteRepository = favoriteRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.messageService = messageService;
        this.itemService = itemService;
        this.userProfileService = userProfileService;
    }

    @Override
    @Transactional
    public ItemDTO addFavorite(Long itemId) {
        logger.info("添加收藏: itemId={}", itemId);
        try {
            // 获取当前用户
            User currentUser = userService.findByUsername(userService.getCurrentUser().getUsername());
            logger.info("当前用户: id={}, username={}", currentUser.getId(), currentUser.getUsername());
            
            // 获取物品
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> {
                        logger.error("物品不存在: itemId={}", itemId);
                        return new EntityNotFoundException("物品不存在");
                    });
            logger.info("物品信息: id={}, name={}", item.getId(), item.getName());
            
            // 检查是否已收藏
            if (favoriteRepository.existsByUserAndItem(currentUser, item)) {
                logger.warn("用户已收藏过该物品: userId={}, itemId={}", currentUser.getId(), itemId);
                throw new IllegalStateException("您已收藏过该物品");
            }
            
            // 创建收藏
            Favorite favorite = Favorite.builder()
                    .user(currentUser)
                    .item(item)
                    .build();
            
            Favorite savedFavorite = favoriteRepository.save(favorite);
            logger.info("收藏成功: favoriteId={}", savedFavorite.getId());
            
            // 创建收藏消息通知
            if (!currentUser.getId().equals(item.getUser().getId())) {
                try {
                    logger.info("开始创建收藏消息: 发送者={}, 接收者={}", currentUser.getUsername(), item.getUser().getUsername());
                    
                    FavoriteMessage favoriteMessage = new FavoriteMessage();
                    favoriteMessage.setSender(currentUser);
                    favoriteMessage.setRecipient(item.getUser());
                    favoriteMessage.setItem(item);
                    favoriteMessage.setFavorite(savedFavorite);
                    favoriteMessage.setRead(false);
                    
                    FavoriteMessage savedMessage = messageService.saveFavoriteMessage(favoriteMessage);
                    logger.info("收藏消息创建成功: messageId={}, senderId={}, recipientId={}, itemId={}",
                            savedMessage.getId(), currentUser.getId(), item.getUser().getId(), item.getId());
                } catch (Exception e) {
                    logger.error("创建收藏消息失败: {}", e.getMessage(), e);
                    // 不影响主流程，继续执行
                }
            } else {
                logger.info("用户收藏了自己的物品，不创建消息通知");
            }
            
            // 添加收藏后自动更新用户画像（如有需要）
            if (currentUser.isAllowPersonalizedRecommend()) {
                userProfileService.updateProfile(currentUser);
            }
            
            // 转换为DTO返回
            ItemDTO itemDTO = convertItemToDTO(item);
            itemDTO.setFavoriteId(savedFavorite.getId());
            return itemDTO;
        } catch (Exception e) {
            logger.error("添加收藏失败: itemId={}", itemId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean removeFavorite(Long favoriteId) {
        logger.info("取消收藏: favoriteId={}", favoriteId);
        try {
            // 获取当前用户
            User currentUser = userService.findByUsername(userService.getCurrentUser().getUsername());
            logger.info("当前用户: id={}, username={}", currentUser.getId(), currentUser.getUsername());
            
            // 获取收藏
            Favorite favorite = favoriteRepository.findById(favoriteId)
                    .orElseThrow(() -> {
                        logger.error("收藏不存在: favoriteId={}", favoriteId);
                        return new EntityNotFoundException("收藏不存在");
                    });
            logger.info("收藏信息: id={}, itemId={}", favorite.getId(), favorite.getItem().getId());
            
            // 检查是否是当前用户的收藏
            if (!favorite.getUser().getId().equals(currentUser.getId())) {
                logger.warn("无权限操作此收藏: favoriteId={}, userId={}, favoriteUserId={}", 
                        favoriteId, currentUser.getId(), favorite.getUser().getId());
                throw new IllegalStateException("无权限操作此收藏");
            }
            
            // 删除收藏
            favoriteRepository.delete(favorite);
            logger.info("取消收藏成功: favoriteId={}", favoriteId);
            
            // 取消收藏后自动更新用户画像（如有需要）
            if (currentUser.isAllowPersonalizedRecommend()) {
                userProfileService.updateProfile(currentUser);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("取消收藏失败: favoriteId={}", favoriteId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean removeFavoriteByItemId(Long itemId) {
        logger.info("根据物品ID取消收藏: itemId={}", itemId);
        try {
            // 获取当前用户
            User currentUser = userService.findByUsername(userService.getCurrentUser().getUsername());
            logger.info("当前用户: id={}, username={}", currentUser.getId(), currentUser.getUsername());
            
            // 获取物品
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> {
                        logger.error("物品不存在: itemId={}", itemId);
                        return new EntityNotFoundException("物品不存在");
                    });
            logger.info("物品信息: id={}, name={}", item.getId(), item.getName());
            
            // 获取收藏
            Favorite favorite = favoriteRepository.findByUserAndItem(currentUser, item)
                    .orElse(null);
            
            if (favorite == null) {
                logger.warn("收藏不存在: userId={}, itemId={}", currentUser.getId(), itemId);
                // 不抛出异常，直接返回成功
                return true;
            }
            
            logger.info("收藏信息: id={}", favorite.getId());
            
            // 删除收藏
            favoriteRepository.delete(favorite);
            logger.info("根据物品ID取消收藏成功: itemId={}, favoriteId={}", itemId, favorite.getId());
            
            // 取消收藏后自动更新用户画像（如有需要）
            if (currentUser.isAllowPersonalizedRecommend()) {
                userProfileService.updateProfile(currentUser);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("根据物品ID取消收藏失败: itemId={}, 错误信息: {}", itemId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PageResponseDTO<ItemDTO> getUserFavorites(int pageNum, int pageSize) {
        logger.info("获取用户收藏列表: pageNum={}, pageSize={}", pageNum, pageSize);
        try {
            // 获取当前用户
            User currentUser = userService.findByUsername(userService.getCurrentUser().getUsername());
            logger.info("当前用户: id={}, username={}", currentUser.getId(), currentUser.getUsername());
            
            // 创建分页参数
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
            
            // 查询收藏
            Page<Favorite> favoritesPage = favoriteRepository.findByUser(currentUser, pageable);
            logger.info("查询到收藏数量: total={}", favoritesPage.getTotalElements());
            
            // 转换为DTO
            List<ItemDTO> itemDTOs = favoritesPage.getContent().stream()
                    .map(favorite -> {
                        ItemDTO dto = itemService.convertToDTO(favorite.getItem());
                        // 设置收藏ID
                        dto.setFavoriteId(favorite.getId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            // 构建分页响应
            PageResponseDTO<ItemDTO> pageResponseDTO = new PageResponseDTO<>();
            pageResponseDTO.setList(itemDTOs);
            pageResponseDTO.setTotal(favoritesPage.getTotalElements());
            pageResponseDTO.setPageNum(pageNum);
            pageResponseDTO.setPageSize(pageSize);
            pageResponseDTO.setPages(favoritesPage.getTotalPages());
            
            logger.info("获取用户收藏列表成功: size={}", itemDTOs.size());
            return pageResponseDTO;
        } catch (Exception e) {
            logger.error("获取用户收藏列表失败: pageNum={}, pageSize={}", pageNum, pageSize, e);
            throw e;
        }
    }

    @Override
    public ItemDTO checkFavoriteStatus(Long itemId) {
        logger.info("检查物品是否已收藏: itemId={}", itemId);
        try {
            // 获取当前用户
            User currentUser = userService.findByUsername(userService.getCurrentUser().getUsername());
            logger.info("当前用户: id={}, username={}", currentUser.getId(), currentUser.getUsername());
            
            // 获取物品
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> {
                        logger.error("物品不存在: itemId={}", itemId);
                        return new EntityNotFoundException("物品不存在");
                    });
            logger.info("物品信息: id={}, name={}", item.getId(), item.getName());
            
            // 检查是否已收藏
            Favorite favorite = favoriteRepository.findByUserAndItem(currentUser, item).orElse(null);
            if (favorite == null) {
                logger.info("用户未收藏该物品: userId={}, itemId={}", currentUser.getId(), itemId);
                return null;
            }
            
            // 转换为DTO返回
            ItemDTO itemDTO = convertItemToDTO(item);
            // 设置收藏ID
            itemDTO.setFavoriteId(favorite.getId());
            logger.info("用户已收藏该物品: userId={}, itemId={}, favoriteId={}", 
                    currentUser.getId(), itemId, favorite.getId());
            
            return itemDTO;
        } catch (Exception e) {
            logger.error("检查物品是否已收藏失败: itemId={}", itemId, e);
            throw e;
        }
    }
    
    /**
     * 将Item实体转换为ItemDTO
     * @param item 物品实体
     * @return 物品DTO
     */
    private ItemDTO convertItemToDTO(Item item) {
        ItemDTO dto = ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .description(item.getDescription())
                .condition(item.getItemCondition())
                .status(item.getStatus())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .userId(item.getUser().getId())
                .username(item.getUser().getUsername())
                .createTime(item.getCreateTime())
                .updateTime(item.getUpdateTime())
                .popularity(item.getPopularity())
                .imageUrls(item.getImageIds())
                .build();
        logger.debug("Item转换为DTO: itemId={}, name={}", item.getId(), item.getName());
        return dto;
    }

    @Override
    public User getCurrentUser(Principal principal) {
        if (principal == null) throw new RuntimeException("未登录");
        return userService.findByUsername(principal.getName());
    }
} 