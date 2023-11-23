package ru.practicum.model.comment;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constants.DateTimeFormatConstants;

import java.time.LocalDateTime;

@Data
public abstract class CommentSearchBaseFilter {
    private String text;
    private Boolean hasParticipated;

    @DateTimeFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime rangeEnd;
}