package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ParentCommentDto {
    private Long id;
    private String comment;
}