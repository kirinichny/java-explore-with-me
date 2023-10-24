package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryCreateOrUpdateDto;
import ru.practicum.dto.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto getCategoryById(long categoryId);

    List<CategoryResponseDto> getCategories(Pageable pageable);

    CategoryResponseDto createCategory(CategoryCreateOrUpdateDto category);

    CategoryResponseDto updateCategory(long categoryId, CategoryCreateOrUpdateDto category);

    void deleteCategory(long categoryId);
}
