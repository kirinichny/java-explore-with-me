package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryCreateOrUpdateDto;
import ru.practicum.dto.category.CategoryResponseDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.category.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto getCategoryById(long categoryId) {
        return categoryMapper.toCategoryResponseDto(getCategoryByIdOrThrow(categoryId));
    }

    @Override
    public List<CategoryResponseDto> getCategories(Pageable pageable) {
        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categoryMapper.toCategoryResponseDto(categories);
    }

    @Override
    public CategoryResponseDto createCategory(CategoryCreateOrUpdateDto category) {
        return categoryMapper.toCategoryResponseDto(
                categoryRepository.save(categoryMapper.toCategory(category)));
    }

    @Override
    public CategoryResponseDto updateCategory(long categoryId, CategoryCreateOrUpdateDto category) {
        Category currentCategory = getCategoryByIdOrThrow(categoryId);
        currentCategory.setName(category.getName());
        return categoryMapper.toCategoryResponseDto(categoryRepository.save(currentCategory));
    }

    @Override
    public void deleteCategory(long categoryId) {
        categoryRepository.delete(getCategoryByIdOrThrow(categoryId));
    }

    private Category getCategoryByIdOrThrow(long categoryId) throws NotFoundException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория #%d не найдена.", categoryId)));
    }
}