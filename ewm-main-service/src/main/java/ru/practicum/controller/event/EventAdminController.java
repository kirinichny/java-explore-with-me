package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventUpdateAdminDto;
import ru.practicum.model.event.EventSearchAdminFilter;
import ru.practicum.service.EventService;
import ru.practicum.validation.ValidationGroup;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEvents(
            @ModelAttribute EventSearchAdminFilter filter,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size
    ) {
        return eventService.searchEvents(filter, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @RequestBody @Validated(ValidationGroup.Update.class) EventUpdateAdminDto event) {
        return eventService.updateEvent(eventId, event);
    }
}
