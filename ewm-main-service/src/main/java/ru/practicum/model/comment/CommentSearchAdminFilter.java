package ru.practicum.model.comment;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentSearchAdminFilter extends CommentSearchBaseFilter {
    private List<Long> authorIds;
    private Boolean onlyAnonymous;
    private Boolean showDeleted;
}