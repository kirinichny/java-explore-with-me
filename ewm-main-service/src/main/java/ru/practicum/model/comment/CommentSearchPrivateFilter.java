package ru.practicum.model.comment;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentSearchPrivateFilter extends CommentSearchBaseFilter {
    private Boolean showDeleted;
}