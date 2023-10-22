package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.constants.DateTimeFormatConstants;
import ru.practicum.dto.category.CategoryResponseDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.event.EventLocation;
import ru.practicum.model.event.EventState;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private CategoryResponseDto category;

    @JsonFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime eventDate;

    private String title;
    private String annotation;
    private String description;
    private EventLocation location;
    private UserShortDto initiator;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private EventState state;
    private LocalDateTime publishedOn;
    private LocalDateTime createdOn;
    private Long confirmedRequests;
    private Long views;
}
