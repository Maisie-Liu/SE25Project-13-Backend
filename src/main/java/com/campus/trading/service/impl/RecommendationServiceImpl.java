package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.entity.User;
import com.campus.trading.entity.UserProfile;
import com.campus.trading.entity.Item;
import com.campus.trading.repository.UserProfileRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.RecommendationService;
import com.campus.trading.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final UserProfileRepository userProfileRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Autowired
    public RecommendationServiceImpl(UserProfileRepository userProfileRepository,
                                     ItemRepository itemRepository,
                                     ItemService itemService) {
        this.userProfileRepository = userProfileRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @Override
    public Page<ItemDTO> recommendItems(User user, int pageNum, int pageSize) {
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser(user);
        Page<Item> itemPage;
        if (profileOpt.isEmpty() || profileOpt.get().getCategoryInterest().isEmpty()) {
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "popularity"));
            itemPage = itemRepository.findAll(pageable);
        } else {
            UserProfile profile = profileOpt.get();
            List<Long> topCategories = profile.getCategoryInterest().entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            itemPage = itemRepository.findByCategoryIdInAndStatusOrderByPopularityDesc(topCategories, 1, PageRequest.of(pageNum - 1, pageSize));
        }
        return itemPage.map(itemService::convertToDTO);
    }
} 