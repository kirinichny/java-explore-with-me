package ru.practicum.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryCreateOrUpdateDto;
import ru.practicum.dto.category.CategoryResponseDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@RequestBody @Valid CategoryCreateOrUpdateDto category) {
        log.debug("+ createCategory: {}", category);
        CategoryResponseDto createdUser = categoryService.createCategory(category);
        log.debug("- createCategory: {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{categoryId}")
    public CategoryResponseDto updateCategory(@PathVariable long categoryId,
                                              @RequestBody @Valid CategoryCreateOrUpdateDto category) {
        log.debug("+ updateCategory: {}", category);
        CategoryResponseDto updatedCategory = categoryService.updateCategory(categoryId, category);
        log.debug("- updateCategory: {}", updatedCategory);
        return updatedCategory;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long categoryId) {
        log.debug("+ deleteCategory: CategoryId={}", categoryId);
        categoryService.deleteCategory(categoryId);
        log.debug("- deleteCategory");
    }
}
