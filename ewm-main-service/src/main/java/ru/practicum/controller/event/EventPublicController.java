package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.event.EventSearchPublicFilter;
import ru.practicum.service.EventService;
import ru.practicum.service.StatsClientService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPublicController {
    private final EventService eventService;
    private final StatsClientService statsClientService;

    @GetMapping
    public List<EventShortDto> searchEvents(
            @ModelAttribute EventSearchPublicFilter filter,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest request
    ) {
        log.debug("+ getCategories");
        Pageable pageable = PageRequest.of(from / size, size);
        statsClientService.addHit(request);
        List<EventShortDto> events = eventService.searchEvents(filter, pageable);
        log.debug("- getCategories: {}", events);
        return events;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId, HttpServletRequest request) {
        log.debug("+ getEventById: eventId={}", eventId);
        statsClientService.addHit(request);
        EventFullDto event = eventService.getPublishedEventById(eventId);
        log.debug("- getEventById: {}", event);
        return event;
    }
}