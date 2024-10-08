package com.example.shopapp.services;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;
import com.example.shopapp.repositories.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// Lombok will generate a constructor with all the required fields (final fields) of the class
public class CategoryServiceImpl implements CategoryService {

    // CategoryRepository will be injected by Spring
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(); // throw NoSuchElementException if category is not found
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        // delete category by id
        categoryRepository.deleteById(id);
    }
}
