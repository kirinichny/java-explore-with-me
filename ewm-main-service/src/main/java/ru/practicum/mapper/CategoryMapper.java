package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.category.CategoryCreateOrUpdateDto;
import ru.practicum.dto.category.CategoryResponseDto;
import ru.practicum.model.category.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryCreateOrUpdateDto category);

    CategoryResponseDto toCategoryResponseDto(Category category);

    List<CategoryResponseDto> toCategoryResponseDto(List<Category> categories);
}
