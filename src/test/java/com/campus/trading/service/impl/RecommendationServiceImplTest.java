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
import static org.mockito.ArgumentMatchers.any;
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
    void recommendItems() {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<com.campus.trading.entity.Item> itemPage = new org.springframework.data.domain.PageImpl<>(
                java.util.Collections.emptyList(),
                pageable,
                0
        );
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(itemPage);
        when(itemService.convertToDTO(any())).thenReturn(new com.campus.trading.dto.ItemDTO());
        com.campus.trading.entity.User user = new com.campus.trading.entity.User();
        assertNotNull(recommendationService.recommendItems(user, 1, 10));
    }
}