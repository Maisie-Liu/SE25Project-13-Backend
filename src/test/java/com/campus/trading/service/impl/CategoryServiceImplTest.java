package com.campus.trading.service.impl;

import com.campus.trading.service.CategoryService;
import com.campus.trading.entity.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceImplTest {
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private CategoryServiceImplTest testInstance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllCategories() {
        when(categoryService.getAllCategories()).thenReturn(java.util.Collections.emptyList());
        assertNotNull(categoryService.getAllCategories());
    }

    @Test
    void findById() {
        when(categoryService.findById(anyLong())).thenReturn(new Category());
        assertNotNull(categoryService.findById(1L));
    }
}