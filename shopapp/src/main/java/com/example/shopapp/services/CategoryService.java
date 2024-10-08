package com.example.shopapp.services;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long id, CategoryDTO category);
    void deleteCategory(Long id);
}
