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
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

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
    void getProfileDTO() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setCategoryInterest(new java.util.HashMap<>());
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(categoryRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        assertNotNull(userProfileService.getProfileDTO(user));
    }

    @Test
    void addReputationScore() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(90);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.addReputationScore(user, 5, "test");
        assertEquals(95, profile.getReputationScore());
    }

    @Test
    void deductReputationScore() {
        User user = new User();
        UserProfile profile = new UserProfile();
        profile.setReputationScore(90);
        when(userProfileRepository.findByUser(any(User.class))).thenReturn(java.util.Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        userProfileService.deductReputationScore(user, 10, "test");
        assertEquals(80, profile.getReputationScore());
    }
}