package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.event.Event;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "annotation", target = "annotation")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "participantLimit", target = "participantLimit", defaultValue = "0")
    @Mapping(source = "paid", target = "isPaymentRequired", defaultExpression = "java(Boolean.FALSE)")
    @Mapping(source = "requestModeration", target = "requestModeration", defaultExpression = "java(Boolean.TRUE)")
    @Mapping(target = "confirmedRequestsCount", constant = "0")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    Event toEvent(EventCreateDto event);

    @Mapping(source = "isPaymentRequired", target = "paid")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "confirmedRequestsCount", target = "confirmedRequests")
    EventFullDto toEventFullDto(Event event);

    @Mapping(source = "isPaymentRequired", target = "paid")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "confirmedRequestsCount", target = "confirmedRequests")
    EventShortDto toEventShortDto(Event event);

    List<EventFullDto> toEventFullDto(List<Event> events);

    List<EventShortDto> toEventShortDto(List<Event> events);
}
