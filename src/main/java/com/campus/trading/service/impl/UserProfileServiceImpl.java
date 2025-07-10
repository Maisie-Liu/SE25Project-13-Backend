package com.campus.trading.service.impl;

import com.campus.trading.entity.UserProfile;
import com.campus.trading.entity.User;
import com.campus.trading.entity.UserView;
import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.Order;
import com.campus.trading.entity.Item;
import com.campus.trading.dto.UserProfileDTO;
import com.campus.trading.entity.Category;
import com.campus.trading.repository.UserProfileRepository;
import com.campus.trading.repository.UserViewRepository;
import com.campus.trading.repository.FavoriteRepository;
import com.campus.trading.repository.OrderRepository;
import com.campus.trading.repository.CategoryRepository;
import com.campus.trading.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserViewRepository userViewRepository;
    private final FavoriteRepository favoriteRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  UserViewRepository userViewRepository,
                                  FavoriteRepository favoriteRepository,
                                  OrderRepository orderRepository,
                                  CategoryRepository categoryRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userViewRepository = userViewRepository;
        this.favoriteRepository = favoriteRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public UserProfile getOrCreateProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile profile = UserProfile.builder()
                            .user(user)
                            .categoryInterest(new HashMap<>())
                            .updateTime(LocalDateTime.now())
                            .build();
                    return userProfileRepository.save(profile);
                });
    }

    @Override
    @Transactional
    public void updateProfile(User user) {
        UserProfile profile = getOrCreateProfile(user);
        Map<Long, Double> interest = new HashMap<>();

        // 浏览行为权重
        List<UserView> views = userViewRepository.findByUser(user);
        for (UserView view : views) {
            Item item = view.getItem();
            if (item != null && item.getCategory() != null) {
                Long catId = item.getCategory().getId();
                interest.put(catId, interest.getOrDefault(catId, 0.0) + 1.0);
            }
        }
        // 收藏行为权重
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        for (Favorite fav : favorites) {
            Item item = fav.getItem();
            if (item != null && item.getCategory() != null) {
                Long catId = item.getCategory().getId();
                interest.put(catId, interest.getOrDefault(catId, 0.0) + 3.0);
            }
        }
        // 交易行为权重
        List<Order> orders = orderRepository.findByBuyer(user);
        for (Order order : orders) {
            Item item = order.getItem();
            if (item != null && item.getCategory() != null) {
                Long catId = item.getCategory().getId();
                interest.put(catId, interest.getOrDefault(catId, 0.0) + 5.0);
            }
        }
        // 可根据需要归一化或加权
        profile.setCategoryInterest(interest);
        profile.setUpdateTime(LocalDateTime.now());
        userProfileRepository.save(profile);
    }

    @Override
    public UserProfileDTO getProfileDTO(User user) {
        UserProfile profile = getOrCreateProfile(user);
        Map<Long, Double> interest = profile.getCategoryInterest();
        Map<Long, String> idToName = new HashMap<>();
        if (interest != null) {
            for (Long catId : interest.keySet()) {
                Category cat = categoryRepository.findById(catId).orElse(null);
                idToName.put(catId, cat != null ? cat.getName() : String.valueOf(catId));
            }
        }
        return UserProfileDTO.builder()
                .userId(user.getId())
                .updateTime(profile.getUpdateTime())
                .categoryIdToName(idToName)
                .categoryInterest(interest)
                .build();
    }
} 