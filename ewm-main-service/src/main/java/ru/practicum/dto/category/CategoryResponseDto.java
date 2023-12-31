package ru.practicum.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}