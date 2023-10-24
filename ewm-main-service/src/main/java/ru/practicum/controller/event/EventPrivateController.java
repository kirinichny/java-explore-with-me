package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventUpdateUserDto;
import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusDto;
import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusResponseDto;
import ru.practicum.dto.participationRequest.ParticipationRequestResponseDto;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestService;
import ru.practicum.validation.ValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventsByInitiatorId(
            @PathVariable(name = "userId") long initiatorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("+ getEventsByInitiatorId: initiatorId={}", initiatorId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> events = eventService.getEventsByInitiatorId(initiatorId, pageable);
        log.debug("- getEventsByInitiatorId: {}", events);
        return events;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId, @PathVariable long userId) {
        log.debug("+ getEventById: eventId={}", eventId);
        EventFullDto event = eventService.getEventById(eventId, userId);
        log.debug("- getEventById: {}", event);
        return event;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestResponseDto> getParticipationRequestsEventById(
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        log.debug("+ getParticipationRequestsEventById: eventId={}, userId={}", eventId, userId);
        List<ParticipationRequestResponseDto> requests = requestService.getRequestsByEventId(eventId, userId);
        log.debug("- getParticipationRequestsEventById: {}", requests);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Validated(ValidationGroup.Create.class) EventCreateDto event,
                                    @PathVariable long userId) {
        log.debug("+ createEvent");
        EventFullDto createdEvent = eventService.createEvent(event, userId);
        log.debug("- createEvent: {}", createdEvent);
        return createdEvent;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @RequestBody @Validated(ValidationGroup.Update.class) EventUpdateUserDto event,
                                    @PathVariable long userId) {
        log.debug("+ updateEvent: eventId={}", eventId);
        EventFullDto updatedEvent = eventService.updateEvent(eventId, event, userId);
        log.debug("- updateEvent: {}", updatedEvent);
        return updatedEvent;
    }

    @PatchMapping("/{eventId}/requests")
    public ParticipationRequestChangeStatusResponseDto updateRequestStatuses(
            @RequestBody @Valid ParticipationRequestChangeStatusDto statusData,
            @PathVariable long eventId,
            @PathVariable long userId) {
        log.debug("+ updateRequestStatuses: eventId={}", eventId);
        ParticipationRequestChangeStatusResponseDto updatedRequest = requestService.updateRequestStatuses(statusData, eventId, userId);
        log.debug("- updateRequestStatuses");
        return updatedRequest;
    }
}