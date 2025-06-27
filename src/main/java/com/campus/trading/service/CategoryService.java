package com.campus.trading.service;

import com.campus.trading.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    Category findById(Long categoryId);
}