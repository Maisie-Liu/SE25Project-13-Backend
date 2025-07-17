package com.campus.trading.service.impl;

import com.campus.trading.dto.UserProfileDTO;
import com.campus.trading.entity.User;
import com.campus.trading.entity.UserProfile;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.campus.trading.repository.UserProfileRepository;
import com.campus.trading.repository.UserViewRepository;
import com.campus.trading.repository.FavoriteRepository;
import com.campus.trading.repository.OrderRepository;
import com.campus.trading.repository.CategoryRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ActiveProfiles("test")
class UserProfileServiceImplTest {
    @MockBean
    private UserProfileRepository userProfileRepository;
    @MockBean
    private UserViewRepository userViewRepository;
    @MockBean
    private FavoriteRepository favoriteRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Test
    void getOrCreateProfile_found() {
        User user = new User();
        UserProfile profile = new UserProfile();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        assertNotNull(userProfileService.getOrCreateProfile(user));
    }

    @Test
    void getOrCreateProfile_notFound() {
        User user = new User();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(new UserProfile());
        assertNotNull(userProfileService.getOrCreateProfile(user));
    }

    @Test
    void getOrCreateProfile_userNull() {
        when(userProfileRepository.findByUser(isNull())).thenReturn(java.util.Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(new UserProfile());
        assertNotNull(userProfileService.getOrCreateProfile(null));
    }

    @Test
    void getProfileDTO_foundWithEmptyCategory() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setCategoryInterest(new java.util.HashMap<>());
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(categoryRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        assertNotNull(userProfileService.getProfileDTO(user));
    }

    @Test
    void getProfileDTO_profileNotFound() {
        User user = new User();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(new UserProfile());
        assertNotNull(userProfileService.getProfileDTO(user));
    }

    @Test
    void getProfileDTO_categoryInterestMulti() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        java.util.Map<Long, Double> interest = new java.util.HashMap<>();
        interest.put(1L, 2.0);
        interest.put(2L, 3.0);
        profile.setCategoryInterest(interest);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        // 1L 存在，2L 不存在
        com.campus.trading.entity.Category cat = new com.campus.trading.entity.Category();
        cat.setId(1L);
        cat.setName("电子产品");
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(cat));
        when(categoryRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        UserProfileDTO dto = userProfileService.getProfileDTO(user);
        assertNotNull(dto);
        assertTrue(dto.getCategoryInterest().containsKey(1L));
        assertTrue(dto.getCategoryInterest().containsKey(2L));
    }

    @Test
    void addReputationScore_normal() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(90);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.addReputationScore(user, 5, "test");
        assertEquals(95, profile.getReputationScore());
    }

    @Test
    void addReputationScore_toMax100() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(98);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.addReputationScore(user, 10, "test");
        assertEquals(100, profile.getReputationScore());
    }

    @Test
    void addReputationScore_negative() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(90);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.addReputationScore(user, -5, "test");
        assertEquals(85, profile.getReputationScore());
    }

    @Test
    void deductReputationScore_normal() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(90);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.deductReputationScore(user, 10, "test");
        assertEquals(80, profile.getReputationScore());
    }

    @Test
    void deductReputationScore_toMin0() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(5);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.deductReputationScore(user, 10, "test");
        assertEquals(0, profile.getReputationScore());
    }

    @Test
    void deductReputationScore_negative() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(90);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.deductReputationScore(user, -10, "test");
        assertEquals(100, profile.getReputationScore());
    }

    @Test
    void updateProfile_allEmpty() {
        User user = new User();
        UserProfile profile = new UserProfile();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userViewRepository.findByUser(any(User.class))).thenReturn(java.util.Collections.emptyList());
        when(favoriteRepository.findByUser(any(User.class))).thenReturn(java.util.Collections.emptyList());
        when(orderRepository.findByBuyer(any(User.class))).thenReturn(java.util.Collections.emptyList());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        assertDoesNotThrow(() -> userProfileService.updateProfile(user));
    }

    @Test
    void updateProfile_withAllBehaviors() {
        User user = new User();
        UserProfile profile = new UserProfile();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        // 浏览
        com.campus.trading.entity.Item item1 = new com.campus.trading.entity.Item();
        com.campus.trading.entity.Category cat1 = new com.campus.trading.entity.Category();
        cat1.setId(1L);
        item1.setCategory(cat1);
        com.campus.trading.entity.UserView view = new com.campus.trading.entity.UserView();
        view.setItem(item1);
        when(userViewRepository.findByUser(any(User.class))).thenReturn(java.util.Collections.singletonList(view));
        // 收藏
        com.campus.trading.entity.Favorite fav = new com.campus.trading.entity.Favorite();
        fav.setItem(item1);
        when(favoriteRepository.findByUser(any(User.class))).thenReturn(java.util.Collections.singletonList(fav));
        // 交易
        com.campus.trading.entity.Order order = new com.campus.trading.entity.Order();
        order.setItem(item1);
        when(orderRepository.findByBuyer(any(User.class))).thenReturn(java.util.Collections.singletonList(order));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        assertDoesNotThrow(() -> userProfileService.updateProfile(user));
    }

    @Test
    void updateProfile_nullItemOrCategory() {
        User user = new User();
        UserProfile profile = new UserProfile();
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        // 浏览行为 item 为 null
        com.campus.trading.entity.UserView view = new com.campus.trading.entity.UserView();
        view.setItem(null);
        when(userViewRepository.findByUser(any(User.class))).thenReturn(java.util.Collections.singletonList(view));
        // 收藏行为 item.category 为 null
        com.campus.trading.entity.Item item = new com.campus.trading.entity.Item();
        item.setCategory(null);
        com.campus.trading.entity.Favorite fav = new com.campus.trading.entity.Favorite();
        fav.setItem(item);
        when(favoriteRepository.findByUser(any(User.class))).thenReturn(java.util.Collections.singletonList(fav));
        // 交易行为 item/category 都为 null
        com.campus.trading.entity.Order order = new com.campus.trading.entity.Order();
        order.setItem(null);
        when(orderRepository.findByBuyer(any(User.class))).thenReturn(java.util.Collections.singletonList(order));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        assertDoesNotThrow(() -> userProfileService.updateProfile(user));
    }
}