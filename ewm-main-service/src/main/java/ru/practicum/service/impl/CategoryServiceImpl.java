package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto getCategoryById(long categoryId) {
        log.debug("+ getCategoryById: categoryId={}", categoryId);
        CategoryResponseDto category = categoryMapper.toCategoryResponseDto(getCategoryByIdOrThrow(categoryId));
        log.debug("- getCategoryById: {}", category);
        return category;
    }

    @Override
    public List<CategoryResponseDto> getCategories(Integer from, Integer size) {
        log.debug("+ getCategories");
        Pageable pageable = PageRequest.of(from / size, size);
        List<CategoryResponseDto> categories = categoryMapper.toCategoryResponseDto(
                categoryRepository.findAll(pageable).getContent());
        log.debug("- getCategories: {}", categories);
        return categories;
    }

    @Override
    public CategoryResponseDto createCategory(CategoryCreateOrUpdateDto category) {
        log.debug("+ createCategory: {}", category);
        CategoryResponseDto createdUser = categoryMapper.toCategoryResponseDto(
                categoryRepository.save(categoryMapper.toCategory(category)));
        log.debug("- createCategory: {}", createdUser);
        return createdUser;
    }

    @Override
    public CategoryResponseDto updateCategory(long categoryId, CategoryCreateOrUpdateDto category) {
        log.debug("+ updateCategory: {}", category);
        Category currentCategory = getCategoryByIdOrThrow(categoryId);
        currentCategory.setName(category.getName());
        CategoryResponseDto updatedCategory = categoryMapper.toCategoryResponseDto(categoryRepository.save(currentCategory));
        log.debug("- updateCategory: {}", updatedCategory);
        return updatedCategory;
    }

    @Override
    public void deleteCategory(long categoryId) {
        log.debug("+ deleteCategory: CategoryId={}", categoryId);
        categoryRepository.delete(getCategoryByIdOrThrow(categoryId));
        log.debug("- deleteCategory");
    }

    private Category getCategoryByIdOrThrow(long categoryId) throws NotFoundException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория #%d не найдена.", categoryId)));
    }
}