package com.campus.trading.service.impl;

import com.campus.trading.entity.Category;
import com.campus.trading.repository.CategoryRepository;
import com.campus.trading.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
} 