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
    @Autowired
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserRatings() {
        when(ratingRepository.findByUserId(anyLong())).thenReturn(java.util.Collections.emptyList());
        assertNotNull(ratingService.getUserRatings(1L));
    }

    @Test
    void calculateSellerRating() {
        when(ratingRepository.calculateSellerRating(anyLong())).thenReturn(4.5);
        double avg = ratingService.calculateSellerRating(1L);
        assertEquals(4.5, avg);
    }

    @Test
    void calculateBuyerRating() {
        when(ratingRepository.calculateBuyerRating(anyLong())).thenReturn(4.0);
        double avg = ratingService.calculateBuyerRating(1L);
        assertEquals(4.0, avg);
    }
}