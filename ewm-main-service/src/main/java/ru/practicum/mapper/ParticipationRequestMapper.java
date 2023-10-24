package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.participationRequest.ParticipationRequestResponseDto;
import ru.practicum.model.participationRequest.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "createdOn", target = "created")
    ParticipationRequestResponseDto toResponseDto(ParticipationRequest request);

    List<ParticipationRequestResponseDto> toResponseDto(List<ParticipationRequest> requests);
}
