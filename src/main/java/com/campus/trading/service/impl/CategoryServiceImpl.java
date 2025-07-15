package com.campus.trading.service.impl;

import com.campus.trading.entity.Category;
import com.campus.trading.repository.CategoryRepository;
import com.campus.trading.service.CategoryService;
import com.campus.trading.service.impl.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import com.alibaba.fastjson.JSON;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<Category> getAllCategories() {
        String cacheKey = "category:all";
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return JSON.parseArray(cached, Category.class);
        }
        List<Category> categories = categoryRepository.findAll();
        stringRedisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(categories), java.time.Duration.ofMinutes(30));
        return categories;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void refreshCategoryCache() {
        List<Category> categories = categoryRepository.findAll();
        stringRedisTemplate.opsForValue().set("category:all", JSON.toJSONString(categories), java.time.Duration.ofMinutes(30));
    }

    @Override
    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("分类不存在: " + categoryId));
    }
} 