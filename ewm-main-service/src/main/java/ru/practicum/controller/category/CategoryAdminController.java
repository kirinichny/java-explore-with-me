package ru.practicum.controller.category;

import lombok.RequiredArgsConstructor;
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
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@RequestBody @Valid CategoryCreateOrUpdateDto category) {
        return categoryService.createCategory(category);
    }

    @PatchMapping("/{categoryId}")
    public CategoryResponseDto updateCategory(@PathVariable long categoryId,
                                              @RequestBody @Valid CategoryCreateOrUpdateDto category) {
        return categoryService.updateCategory(categoryId, category);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
