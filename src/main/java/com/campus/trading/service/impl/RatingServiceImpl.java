package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.RatingDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.Rating;
import com.campus.trading.entity.User;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.RatingRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.ImageService;
import com.campus.trading.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评分服务实现类
 */
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ImageService imageService;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, 
                           UserRepository userRepository,
                           ItemRepository itemRepository,
                           ImageService imageService) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.imageService = imageService;
    }

    @Override
    public List<RatingDTO> getUserRatings(Long userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        return ratings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Double calculateSellerRating(Long userId) {
        return ratingRepository.calculateSellerRating(userId);
    }

    @Override
    public Double calculateBuyerRating(Long userId) {
        return ratingRepository.calculateBuyerRating(userId);
    }
    
    /**
     * 将实体转换为DTO
     */
    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO ratingDTO = RatingDTO.builder()
                .id(rating.getId())
                .orderId(rating.getOrderId())
                .itemId(rating.getItemId())
                .userId(rating.getUserId())
                .raterId(rating.getRaterId())
                .role(rating.getRole())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createTime(rating.getCreateTime())
                .updateTime(rating.getUpdateTime())
                .build();
        
        // 获取评价者信息
        Optional<User> raterOpt = userRepository.findById(rating.getRaterId());
        if (raterOpt.isPresent()) {
            User rater = raterOpt.get();
            UserDTO raterDTO = UserDTO.builder()
                    .id(rater.getId())
                    .username(rater.getUsername())
                    .nickname(rater.getNickname())
                    .avatarUrl(imageService.generateImageAccessToken(rater.getAvatarImageId()))
                    .build();
            ratingDTO.setRater(raterDTO);
        }
        
        // 获取物品信息
        Optional<Item> itemOpt = itemRepository.findById(rating.getItemId());
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setName(item.getName());
            itemDTO.setPrice(item.getPrice());
            
            // 获取物品图片，如果有的话
            if (item.getImageIds() != null && !item.getImageIds().isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (String imageId : item.getImageIds()) {
                    String imageUrl = imageService.generateImageAccessToken(imageId);
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl);
                    }
                }
                itemDTO.setImages(imageUrls);
            }
            
            ratingDTO.setItem(itemDTO);
        }
        
        return ratingDTO;
    }
} 