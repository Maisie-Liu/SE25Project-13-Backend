package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.entity.User;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.UserProfileRepository;
import com.campus.trading.service.ItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class RecommendationServiceImplTest {

    @MockBean
    private UserProfileRepository userProfileRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ItemService itemService;
    @Autowired
    private RecommendationServiceImpl recommendationService;

    @Test
    void recommendItems_profileNotFound() {
        User user = new User();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.empty());
        Page<com.campus.trading.entity.Item> itemPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(itemPage);
        assertNotNull(recommendationService.recommendItems(user, 1, 10));
    }

    @Test
    void recommendItems_categoryInterestEmpty() {
        User user = new User();
        com.campus.trading.entity.UserProfile profile = new com.campus.trading.entity.UserProfile();
        profile.setCategoryInterest(new java.util.HashMap<>());
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        Page<com.campus.trading.entity.Item> itemPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(itemPage);
        assertNotNull(recommendationService.recommendItems(user, 1, 10));
    }

    @Test
    void recommendItems_categoryInterestMulti() {
        User user = new User();
        com.campus.trading.entity.UserProfile profile = new com.campus.trading.entity.UserProfile();
        java.util.Map<Long, Double> interest = new java.util.HashMap<>();
        interest.put(1L, 2.0);
        interest.put(2L, 3.0);
        profile.setCategoryInterest(interest);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        Page<com.campus.trading.entity.Item> itemPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(itemRepository.findByCategoryIdInAndStatusOrderByPopularityDesc(anyList(), eq(1), any(Pageable.class))).thenReturn(itemPage);
        assertNotNull(recommendationService.recommendItems(user, 1, 10));
    }

    @Test
    void recommendItems_itemRepositoryThrows() {
        User user = new User();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.empty());
        when(itemRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> recommendationService.recommendItems(user, 1, 10));
    }

    @Test
    void recommendItems_itemServiceConvertToDTOReturnsNull() {
        User user = new User();
        com.campus.trading.entity.UserProfile profile = new com.campus.trading.entity.UserProfile();
        java.util.Map<Long, Double> interest = new java.util.HashMap<>();
        interest.put(1L, 2.0);
        profile.setCategoryInterest(interest);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        Page<com.campus.trading.entity.Item> itemPage = new PageImpl<>(Collections.singletonList(new com.campus.trading.entity.Item()), PageRequest.of(0, 10), 1);
        when(itemRepository.findByCategoryIdInAndStatusOrderByPopularityDesc(anyList(), eq(1), any(Pageable.class))).thenReturn(itemPage);
        when(itemService.convertToDTO(any())).thenReturn(null);
        assertNotNull(recommendationService.recommendItems(user, 1, 10));
    }
}