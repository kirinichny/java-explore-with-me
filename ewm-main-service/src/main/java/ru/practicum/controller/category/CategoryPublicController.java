package ru.practicum.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryResponseDto;
import ru.practicum.service.CategoryService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> getCategories(
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("+ getCategories");
        Pageable pageable = PageRequest.of(from / size, size);
        List<CategoryResponseDto> categories = categoryService.getCategories(pageable);
        log.debug("- getCategories: {}", categories);
        return categories;
    }

    @GetMapping("/{categoryId}")
    public CategoryResponseDto getCategoryById(@PathVariable long categoryId) {
        log.debug("+ getCategoryById: categoryId={}", categoryId);
        CategoryResponseDto category = categoryService.getCategoryById(categoryId);
        log.debug("- getCategoryById: {}", category);
        return category;
    }
}
