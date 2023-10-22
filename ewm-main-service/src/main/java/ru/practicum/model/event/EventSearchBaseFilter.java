package ru.practicum.model.event;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.constants.DateTimeFormatConstants;

import java.time.LocalDateTime;
import java.util.List;

@Data
public abstract class EventSearchBaseFilter {
    private List<Long> categories;

    @DateTimeFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime rangeEnd;
}