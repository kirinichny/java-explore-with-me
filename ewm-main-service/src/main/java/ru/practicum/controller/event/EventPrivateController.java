package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
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
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventsByInitiatorId(
            @PathVariable(name = "userId") long initiatorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size
    ) {
        return eventService.getEventsByInitiatorId(initiatorId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId, @PathVariable long userId) {
        return eventService.getEventById(eventId, userId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestResponseDto> getParticipationRequestsEventById(
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        return requestService.getRequestsByEventId(eventId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Validated(ValidationGroup.Create.class) EventCreateDto event,
                                    @PathVariable long userId) {
        return eventService.createEvent(event, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @RequestBody @Validated(ValidationGroup.Update.class) EventUpdateUserDto event,
                                    @PathVariable long userId) {
        return eventService.updateEvent(eventId, event, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public ParticipationRequestChangeStatusResponseDto updateRequestStatuses(
            @RequestBody @Valid ParticipationRequestChangeStatusDto statusData,
            @PathVariable long eventId,
            @PathVariable long userId) {
        return requestService.updateRequestStatuses(statusData, eventId, userId);
    }
}