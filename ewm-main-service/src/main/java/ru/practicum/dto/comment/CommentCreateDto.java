package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {
    @NotNull
    private Long eventId;

    @Positive
    private Long parentCommentId;

    private Boolean isAnonymous;

    @NotBlank
    @Size(max = 2000)
    private String comment;
}