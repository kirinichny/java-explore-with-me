package ru.practicum.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventSearchPublicFilter extends EventSearchBaseFilter {
    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
    private EventSortOption sort;
}