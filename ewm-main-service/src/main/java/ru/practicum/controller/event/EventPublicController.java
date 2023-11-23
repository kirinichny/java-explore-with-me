package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.comment.CommentSearchPublicFilter;
import ru.practicum.model.event.EventSearchPublicFilter;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.service.StatsClientService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;
    private final CommentService commentService;
    private final StatsClientService statsClientService;

    @GetMapping
    public List<EventShortDto> searchEvents(
            @ModelAttribute EventSearchPublicFilter filter,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest request
    ) {
        statsClientService.addHit(request);
        return eventService.searchEvents(filter, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId, HttpServletRequest request) {
        statsClientService.addHit(request);
        return eventService.getPublishedEventById(eventId);
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentResponseDto> searchComments(
            @ModelAttribute CommentSearchPublicFilter filter,
            @PathVariable long eventId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size
    ) {
        return commentService.searchComments(filter, eventId, from, size);
    }

    @GetMapping("/{eventId}/comments/{commentId}")
    public CommentResponseDto getCommentById(@PathVariable long commentId, @PathVariable long eventId) {
        return commentService.getPublishedCommentById(commentId, eventId);
    }
}