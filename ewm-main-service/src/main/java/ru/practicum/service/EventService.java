package ru.practicum.service;

import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventUpdateAdminDto;
import ru.practicum.dto.event.EventUpdateUserDto;
import ru.practicum.model.event.EventSearchAdminFilter;
import ru.practicum.model.event.EventSearchPublicFilter;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEventsByInitiatorId(long initiatorId, Integer from, Integer size);

    EventFullDto getEventById(long eventId, long userId);

    EventFullDto getPublishedEventById(long eventId);

    List<EventFullDto> searchEvents(EventSearchAdminFilter filter, Integer from, Integer size);

    List<EventShortDto> searchEvents(EventSearchPublicFilter filter, Integer from, Integer size);

    EventFullDto createEvent(EventCreateDto event, long userId);

    EventFullDto updateEvent(long eventId, EventUpdateUserDto event, long userId);

    EventFullDto updateEvent(long eventId, EventUpdateAdminDto event);
}
