package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventCreateOrUpdateBaseDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventUpdateAdminDto;
import ru.practicum.dto.event.EventUpdateUserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventSearchAdminFilter;
import ru.practicum.model.event.EventSearchPublicFilter;
import ru.practicum.model.event.EventSortOption;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.EventStateAdminAction;
import ru.practicum.model.event.EventStateUserAction;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventSpecification;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;
import ru.practicum.service.StatsClientService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final StatsClientService statsClientService;

    @Override
    public EventFullDto getEventById(long eventId, long userId) {
        log.debug("+ getEventById: eventId={}", eventId);
        EventFullDto event = eventMapper.toEventFullDto(getEventByInitiatorIdOrThrow(eventId, userId));
        log.debug("- getEventById: {}", event);
        return event;
    }

    @Override
    public EventFullDto getPublishedEventById(long eventId) {
        log.debug("+ getEventById: eventId={}", eventId);
        Event event = getPublishedEventByIdOrThrow(eventId);
        setEventViewsCount(event);
        EventFullDto result = eventMapper.toEventFullDto(event);
        log.debug("- getEventById: {}", result);
        return result;
    }

    @Override
    public List<EventShortDto> getEventsByInitiatorId(long initiatorId, Integer from, Integer size) {
        log.debug("+ getEventsByInitiatorId: initiatorId={}", initiatorId);

        Pageable pageable = PageRequest.of(from / size, size);

        List<EventShortDto> events = eventMapper.toEventShortDto(
                eventRepository.findByInitiatorId(initiatorId, pageable));

        log.debug("- getEventsByInitiatorId: {}", events);

        return events;
    }

    @Override
    public List<EventFullDto> searchEvents(EventSearchAdminFilter filter, Integer from, Integer size) {
        log.debug("+ getCategories");

        Pageable pageable = PageRequest.of(from / size, size);

        Specification<Event> spec = Specification
                .where(EventSpecification.withInitiatorIds(filter.getUsers()))
                .and(EventSpecification.withStates(filter.getStates()))
                .and(EventSpecification.withCategories(filter.getCategories()))
                .and(EventSpecification.withRangeStart(filter.getRangeStart()))
                .and(EventSpecification.withRangeEnd(filter.getRangeEnd()));

        List<EventFullDto> events = eventMapper.toEventFullDto(
                eventRepository.findAll(spec, pageable).getContent());

        log.debug("- getCategories: {}", events);

        return events;
    }

    @Override
    public List<EventShortDto> searchEvents(EventSearchPublicFilter filter, Integer from, Integer size) {
        log.debug("+ getCategories");

        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime rangeStart = filter.getRangeStart();
        LocalDateTime rangeEnd = filter.getRangeEnd();

        if (Objects.isNull(rangeStart) && Objects.isNull(rangeEnd)) {
            rangeStart = LocalDateTime.now();
        }

        if (Objects.nonNull(rangeStart) && Objects.nonNull(rangeEnd) && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Временной диапазон некорректен. Конечная дата" +
                    " не может быть раньше начальной даты.");
        }

        Specification<Event> spec = Specification
                .where(
                        Specification
                                .where(EventSpecification.withAnnotation(filter.getText()))
                                .or(EventSpecification.withDescription(filter.getText()))
                )
                .and(EventSpecification.withCategories(filter.getCategories()))
                .and(EventSpecification.withPaymentRequired(filter.getPaid()))
                .and(EventSpecification.withRangeStart(rangeStart))
                .and(EventSpecification.withRangeEnd(rangeEnd))
                .and(EventSpecification.onlyAvailable(filter.getOnlyAvailable()))
                .and(EventSpecification.withState(EventState.PUBLISHED));

        List<Event> events = sortEvents(eventRepository.findAll(spec, pageable).getContent(), filter.getSort());

        setEventViewsCount(events);

        List<EventShortDto> result = eventMapper.toEventShortDto(events);

        log.debug("- getCategories: {}", result);

        return result;
    }

    @Override
    public EventFullDto createEvent(EventCreateDto event, long userId) {
        log.debug("+ createEvent");

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь #%d не найден.", userId)));

        LocalDateTime currentDate = LocalDateTime.now();
        Event eventToSave = eventMapper.toEvent(event);

        eventToSave.setInitiator(initiator);
        eventToSave.setState(EventState.PENDING);
        eventToSave.setCreatedOn(currentDate);

        EventFullDto createdEvent = eventMapper.toEventFullDto(eventRepository.save(eventToSave));

        log.debug("- createEvent: {}", createdEvent);

        return createdEvent;
    }

    @Override
    public EventFullDto updateEvent(long eventId, EventUpdateUserDto event, long userId) {
        log.debug("+ updateEvent: eventId={}", eventId);

        Event currentEvent = getEventByInitiatorIdOrThrow(eventId, userId);
        EventState currentState = currentEvent.getState();

        if (!currentState.equals(EventState.PENDING) && !currentState.equals(EventState.CANCELED)) {
            throw new DataIntegrityViolationException(
                    String.format("Невозможно изменить событие, так как событие %s", currentState.getName()));
        }

        if (EventStateUserAction.SEND_TO_REVIEW.equals(event.getStateAction())) {
            currentEvent.setState(EventState.PENDING);
        } else if (EventStateUserAction.CANCEL_REVIEW.equals(event.getStateAction())) {
            currentEvent.setState(EventState.CANCELED);
        }

        updateCommonEventFields(currentEvent, event);

        EventFullDto updatedEvent = eventMapper.toEventFullDto(eventRepository.save(currentEvent));

        log.debug("- updateEvent: {}", updatedEvent);

        return updatedEvent;
    }

    @Override
    public EventFullDto updateEvent(long eventId, EventUpdateAdminDto event) {
        log.debug("+ updateEvent: eventId={}", eventId);

        Event currentEvent = getEventByIdOrThrow(eventId);
        EventState currentState = currentEvent.getState();

        if (EventStateAdminAction.PUBLISH_EVENT.equals(event.getStateAction())
                && currentState.equals(EventState.PENDING)) {
            currentEvent.setState(EventState.PUBLISHED);
            currentEvent.setPublishedOn(LocalDateTime.now());
        } else if (EventStateAdminAction.REJECT_EVENT.equals(event.getStateAction())
                && !currentState.equals(EventState.PUBLISHED)) {
            currentEvent.setState(EventState.CANCELED);
        } else if (Objects.nonNull(event.getStateAction())) {
            throw new DataIntegrityViolationException(
                    String.format("Невозможно изменить событие, так как событие %s", currentState.getName()));
        }

        updateCommonEventFields(currentEvent, event);

        EventFullDto updatedEvent = eventMapper.toEventFullDto(eventRepository.save(currentEvent));

        log.debug("- updateEvent: {}", updatedEvent);
        return updatedEvent;
    }

    private void updateCommonEventFields(Event currentEvent, EventCreateOrUpdateBaseDto newEvent) {
        if (Objects.nonNull(newEvent.getCategory())) {
            currentEvent.setCategory(new Category(newEvent.getCategory(), null));
        }

        if (Objects.nonNull(newEvent.getTitle())) {
            currentEvent.setTitle(newEvent.getTitle());
        }

        if (Objects.nonNull(newEvent.getAnnotation())) {
            currentEvent.setAnnotation(newEvent.getAnnotation());
        }

        if (Objects.nonNull(newEvent.getDescription())) {
            currentEvent.setDescription(newEvent.getDescription());
        }

        if (Objects.nonNull(newEvent.getEventDate())) {
            currentEvent.setEventDate(newEvent.getEventDate());
        }

        if (Objects.nonNull(newEvent.getLocation())) {
            currentEvent.setLocation(newEvent.getLocation());
        }

        if (Objects.nonNull(newEvent.getPaid())) {
            currentEvent.setIsPaymentRequired(newEvent.getPaid());
        }

        if (Objects.nonNull(newEvent.getParticipantLimit())) {
            currentEvent.setParticipantLimit(newEvent.getParticipantLimit());
        }

        if (Objects.nonNull(newEvent.getRequestModeration())) {
            currentEvent.setRequestModeration(newEvent.getRequestModeration());
        }
    }

    private Event getEventByIdOrThrow(long eventId) throws NotFoundException {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие #%d не найдено.", eventId)));
    }

    private Event getPublishedEventByIdOrThrow(long eventId) throws NotFoundException {
        return eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Событие #%d не найдено.", eventId)));
    }

    private Event getEventByInitiatorIdOrThrow(Long eventId, Long initiatorId) {
        return eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие #%d у пользователя #%d не найдено.", eventId, initiatorId)));
    }

    private List<Event> sortEvents(List<Event> events, EventSortOption sortOption) {
        if (Objects.isNull(sortOption)) {
            return events;
        }

        switch (sortOption) {
            case EVENT_DATE:
                return events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            case VIEWS:
                return events.stream()
                        .sorted(Comparator.nullsFirst(Comparator.comparing(Event::getViews)))
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Выбранная опция сортировки событий не поддерживается");
        }
    }

    private void setEventViewsCount(List<Event> events) {
        final boolean onlyUniqueHits = true;

        LocalDateTime statsStartDate = events.stream()
                .map(Event::getCreatedOn)
                .min(Comparator.naturalOrder())
                .orElse(null);

        LocalDateTime statsEndDate = LocalDateTime.now();

        List<String> uris = events.stream()
                .map(event -> {
                    String uri = "/events/" + event.getId();
                    event.setViews(0L);
                    return uri;
                })
                .collect(Collectors.toList());

        Map<String, Event> eventsUri = events.stream()
                .collect(Collectors.toMap(event -> "/events/" + event.getId(), event -> event));

        List<ViewStatsDto> stats = statsClientService.getStats(statsStartDate, statsEndDate, uris, onlyUniqueHits);
        stats.forEach(stat -> eventsUri.get(stat.getUri()).setViews(stat.getHits()));
    }

    private void setEventViewsCount(Event event) {
        List<Event> singleEventList = new ArrayList<>();
        singleEventList.add(event);
        setEventViewsCount(singleEventList);
    }
}
