package com.campus.trading.service.impl;

import com.campus.trading.dto.RatingDTO;
import com.campus.trading.entity.Rating;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class RatingServiceImplTest {
    @MockBean
    private com.campus.trading.repository.RatingRepository ratingRepository;
    @MockBean
    private com.campus.trading.repository.UserRepository userRepository;
    @MockBean
    private com.campus.trading.repository.ItemRepository itemRepository;
    @MockBean
    private com.campus.trading.service.ImageService imageService;
    @Autowired
    private RatingServiceImpl ratingService;

    @Test
    void getUserRatings_empty() {
        when(ratingRepository.findByUserId(anyLong())).thenReturn(java.util.Collections.emptyList());
        assertNotNull(ratingService.getUserRatings(1L));
    }

    @Test
    void getUserRatings_withUserAndItem() {
        com.campus.trading.entity.Rating rating = new com.campus.trading.entity.Rating();
        rating.setRaterId(2L);
        rating.setItemId(3L);
        rating.setId(1L);
        rating.setRating(5);
        rating.setComment("好评");
        com.campus.trading.entity.User user = new com.campus.trading.entity.User();
        user.setId(2L);
        user.setUsername("testuser");
        user.setNickname("nick");
        user.setAvatarImageId("imgid");
        com.campus.trading.entity.Item item = new com.campus.trading.entity.Item();
        item.setId(3L);
        item.setName("商品");
        item.setPrice(java.math.BigDecimal.TEN);
        item.setImageIds(java.util.Arrays.asList("imgid1", "imgid2"));
        when(ratingRepository.findByUserId(anyLong())).thenReturn(java.util.Collections.singletonList(rating));
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(3L)).thenReturn(java.util.Optional.of(item));
        when(imageService.generateImageAccessToken(anyString())).thenReturn("url");
        assertNotNull(ratingService.getUserRatings(1L));
    }

    @Test
    void getUserRatings_raterNotFound() {
        com.campus.trading.entity.Rating rating = new com.campus.trading.entity.Rating();
        rating.setRaterId(2L);
        rating.setItemId(3L);
        when(ratingRepository.findByUserId(anyLong())).thenReturn(java.util.Collections.singletonList(rating));
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        when(itemRepository.findById(3L)).thenReturn(java.util.Optional.empty());
        assertNotNull(ratingService.getUserRatings(1L));
    }

    @Test
    void getUserRatings_itemNoImage() {
        com.campus.trading.entity.Rating rating = new com.campus.trading.entity.Rating();
        rating.setRaterId(2L);
        rating.setItemId(3L);
        com.campus.trading.entity.Item item = new com.campus.trading.entity.Item();
        item.setId(3L);
        item.setName("商品");
        item.setPrice(java.math.BigDecimal.TEN);
        item.setImageIds(null);
        when(ratingRepository.findByUserId(anyLong())).thenReturn(java.util.Collections.singletonList(rating));
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        when(itemRepository.findById(3L)).thenReturn(java.util.Optional.of(item));
        assertNotNull(ratingService.getUserRatings(1L));
    }

    @Test
    void calculateSellerRating_normal() {
        when(ratingRepository.calculateSellerRating(anyLong())).thenReturn(4.5);
        double avg = ratingService.calculateSellerRating(1L);
        assertEquals(4.5, avg);
    }

    @Test
    void calculateSellerRating_null() {
        when(ratingRepository.calculateSellerRating(anyLong())).thenReturn(null);
        assertNull(ratingService.calculateSellerRating(1L));
    }

    @Test
    void calculateSellerRating_exception() {
        when(ratingRepository.calculateSellerRating(anyLong())).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> ratingService.calculateSellerRating(1L));
    }

    @Test
    void calculateBuyerRating_normal() {
        when(ratingRepository.calculateBuyerRating(anyLong())).thenReturn(4.0);
        double avg = ratingService.calculateBuyerRating(1L);
        assertEquals(4.0, avg);
    }

    @Test
    void calculateBuyerRating_null() {
        when(ratingRepository.calculateBuyerRating(anyLong())).thenReturn(null);
        assertNull(ratingService.calculateBuyerRating(1L));
    }

    @Test
    void calculateBuyerRating_exception() {
        when(ratingRepository.calculateBuyerRating(anyLong())).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> ratingService.calculateBuyerRating(1L));
    }
}