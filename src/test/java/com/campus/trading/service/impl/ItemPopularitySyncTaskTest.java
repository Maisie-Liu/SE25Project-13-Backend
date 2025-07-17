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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.ItemService;
import static org.mockito.Mockito.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.campus.trading.service.impl.PlatformStatsSyncTask;

@SpringBootTest
@ActiveProfiles("test")
class ItemPopularitySyncTaskTest {
    @Autowired
    private ItemPopularitySyncTask itemPopularitySyncTask;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ItemService itemService;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private PlatformStatsSyncTask platformStatsSyncTask;

    @BeforeEach
    void setUp() {
        // 无需手动openMocks，SpringBootTest自动注入
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void syncPopularityToDb() {
        // mock redis keys
        java.util.Set<String> keys = new java.util.HashSet<>();
        keys.add("item:view:1");
        when(stringRedisTemplate.keys("item:view:*"))
            .thenReturn(keys);

        // mock opsForValue
        org.springframework.data.redis.core.ValueOperations valueOperations = mock(org.springframework.data.redis.core.ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("item:view:1")).thenReturn("10");

        // mock item
        com.campus.trading.entity.Item item = new com.campus.trading.entity.Item();
        item.setId(1L);
        item.setPopularity(5);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.save(any(com.campus.trading.entity.Item.class))).thenReturn(item);

        itemPopularitySyncTask.syncPopularityToDb();

        verify(itemRepository, times(1)).save(any(com.campus.trading.entity.Item.class));
    }
}