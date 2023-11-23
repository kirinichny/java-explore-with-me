package ru.practicum.service;

import ru.practicum.dto.category.CategoryCreateOrUpdateDto;
import ru.practicum.dto.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto getCategoryById(long categoryId);

    List<CategoryResponseDto> getCategories(Integer from, Integer size);

    CategoryResponseDto createCategory(CategoryCreateOrUpdateDto category);

    CategoryResponseDto updateCategory(long categoryId, CategoryCreateOrUpdateDto category);

    void deleteCategory(long categoryId);
}
