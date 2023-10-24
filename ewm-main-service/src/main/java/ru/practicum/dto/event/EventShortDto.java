package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.constants.DateTimeFormatConstants;
import ru.practicum.dto.category.CategoryResponseDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private CategoryResponseDto category;

    @JsonFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime eventDate;

    private String title;
    private String annotation;
    private UserShortDto initiator;
    private Boolean paid;
    private Long confirmedRequests;
    private Long views;
}
