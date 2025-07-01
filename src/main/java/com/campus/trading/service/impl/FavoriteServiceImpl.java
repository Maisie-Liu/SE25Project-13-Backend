package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.FavoriteRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.FavoriteService;
import com.campus.trading.service.UserService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);
    private final FavoriteRepository favoriteRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, ItemRepository itemRepository, UserService userService) {
        this.favoriteRepository = favoriteRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
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
            
            favoriteRepository.save(favorite);
            logger.info("收藏成功: favoriteId={}", favorite.getId());
            
            // 转换为DTO返回
            return convertItemToDTO(item);
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
                    .orElseThrow(() -> {
                        logger.error("收藏不存在: userId={}, itemId={}", currentUser.getId(), itemId);
                        return new EntityNotFoundException("收藏不存在");
                    });
            logger.info("收藏信息: id={}", favorite.getId());
            
            // 删除收藏
            favoriteRepository.delete(favorite);
            logger.info("根据物品ID取消收藏成功: itemId={}", itemId);
            
            return true;
        } catch (Exception e) {
            logger.error("根据物品ID取消收藏失败: itemId={}", itemId, e);
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
                        ItemDTO dto = convertItemToDTO(favorite.getItem());
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
    public boolean checkFavoriteStatus(Long itemId) {
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
            boolean isFavorite = favoriteRepository.existsByUserAndItem(currentUser, item);
            logger.info("检查物品是否已收藏结果: itemId={}, isFavorite={}", itemId, isFavorite);
            
            return isFavorite;
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
} 