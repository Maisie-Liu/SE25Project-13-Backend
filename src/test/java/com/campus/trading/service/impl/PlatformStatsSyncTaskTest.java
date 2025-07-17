package com.campus.trading.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.data.redis.core.HashOperations;

@SpringBootTest
@ActiveProfiles("test")
class PlatformStatsSyncTaskTest {
    @Mock
    private com.campus.trading.repository.ItemRepository itemRepository;
    @Mock
    private com.campus.trading.service.UserService userService;
    @Mock
    private com.campus.trading.repository.OrderRepository orderRepository;
    @Mock
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;
    @InjectMocks
    private PlatformStatsSyncTask platformStatsSyncTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // mock 所有有返回值的方法
        when(itemRepository.countByStatus(anyInt())).thenReturn(0L);
        when(itemRepository.count()).thenReturn(0L);
        when(orderRepository.count()).thenReturn(0L);
        when(userService.getTotalUsers()).thenReturn(0L);
        // mock redis hash操作
        HashOperations hashOperations = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(hashOperations);
        doNothing().when(hashOperations).putAll(any(), anyMap());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() {
        ApplicationArguments args = mock(ApplicationArguments.class);
        assertDoesNotThrow(() -> platformStatsSyncTask.run(args));
    }
}