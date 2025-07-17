package com.campus.trading.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;
import static org.mockito.Mockito.*;
import java.util.Collections;
import com.campus.trading.service.impl.PlatformStatsSyncTask;
import com.campus.trading.service.impl.ItemPopularitySyncTask;

@SpringBootTest
@ActiveProfiles("test")
class RedisPopularityInitializerTest {
    @MockBean
    private com.campus.trading.repository.ItemRepository itemRepository;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private PlatformStatsSyncTask platformStatsSyncTask;
    @MockBean
    private ItemPopularitySyncTask itemPopularitySyncTask;
    @Autowired
    private com.campus.trading.service.impl.RedisPopularityInitializer redisPopularityInitializer;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = org.mockito.MockitoAnnotations.openMocks(this);
        // mock redis hash操作，防止NPE
        HashOperations hashOperations = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(hashOperations);
        doNothing().when(hashOperations).putAll(any(), anyMap());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void run() {
        // mock itemRepository.findAll()
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());
        // mock redis opsForValue
        ValueOperations valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        // 不抛异常即可
        assertDoesNotThrow(() -> redisPopularityInitializer.run(null));
    }
}