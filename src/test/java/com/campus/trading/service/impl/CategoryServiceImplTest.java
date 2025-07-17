package com.campus.trading.service.impl;

import com.alibaba.fastjson.JSON;
import com.campus.trading.entity.Category;
import com.campus.trading.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetAllCategories_CacheHit() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        String json = JSON.toJSONString(categories);
        when(valueOperations.get("category:all")).thenReturn(json);
        List<Category> result = categoryService.getAllCategories();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, never()).findAll();
    }

    @Test
    void testGetAllCategories_CacheMiss() {
        when(valueOperations.get("category:all")).thenReturn(null);
        List<Category> categories = Collections.singletonList(new Category());
        when(categoryRepository.findAll()).thenReturn(categories);
        doNothing().when(valueOperations).set(any(), any(), any());
        List<Category> result = categoryService.getAllCategories();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAll();
        verify(valueOperations, times(1)).set(any(), any(), any());
    }

    @Test
    void testFindById_Found() {
        Category category = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Category result = categoryService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindById_NotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoryService.findById(1L));
        assertTrue(ex.getMessage().contains("分类不存在"));
    }

    @Test
    void testRefreshCategoryCache() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryRepository.findAll()).thenReturn(categories);
        doNothing().when(valueOperations).set(any(), any(), any());
        categoryService.refreshCategoryCache();
        verify(categoryRepository, times(1)).findAll();
        verify(valueOperations, times(1)).set(eq("category:all"), any(), any());
    }
}