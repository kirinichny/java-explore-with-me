package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.constants.DateTimeFormatConstants;
import ru.practicum.model.event.EventLocation;
import ru.practicum.validation.ValidationGroup;
import ru.practicum.validation.event.EventDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public abstract class EventCreateOrUpdateBaseDto {
    @Positive(groups = {ValidationGroup.Create.class})
    private Long category;

    @NotNull(groups = {ValidationGroup.Create.class})
    @EventDateValidation(groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
    @JsonFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime eventDate;

    @NotBlank(groups = {ValidationGroup.Create.class})
    @Size(
            groups = {ValidationGroup.Create.class, ValidationGroup.Update.class},
            min = 3, max = 120
    )
    private String title;

    @NotBlank(groups = {ValidationGroup.Create.class})
    @Size(
            groups = {ValidationGroup.Create.class, ValidationGroup.Update.class},
            min = 20, max = 2000
    )
    private String annotation;

    @NotBlank(groups = {ValidationGroup.Create.class})
    @Size(
            groups = {ValidationGroup.Create.class, ValidationGroup.Update.class},
            min = 20, max = 7000
    )
    private String description;

    @NotNull(groups = {ValidationGroup.Create.class})
    private EventLocation location;

    @PositiveOrZero(groups = {ValidationGroup.Create.class})
    private Integer participantLimit;

    private Boolean paid;
    private Boolean requestModeration;
}