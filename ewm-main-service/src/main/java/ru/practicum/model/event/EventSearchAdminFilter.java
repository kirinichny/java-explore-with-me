package ru.practicum.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class EventSearchAdminFilter extends EventSearchBaseFilter {
    private List<Long> users;
    private List<EventState> states;
}