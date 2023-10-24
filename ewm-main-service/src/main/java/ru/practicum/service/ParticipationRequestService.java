package ru.practicum.service;

import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusDto;
import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusResponseDto;
import ru.practicum.dto.participationRequest.ParticipationRequestResponseDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestResponseDto> getRequestsByEventId(long eventId, long userId);

    List<ParticipationRequestResponseDto> getRequestsByRequesterId(long requesterId);

    ParticipationRequestResponseDto createRequest(long eventId, long userId);

    ParticipationRequestChangeStatusResponseDto updateRequestStatuses(ParticipationRequestChangeStatusDto statusData,
                                                                      long eventId, long userId);

    ParticipationRequestResponseDto cancelRequest(long requestId, long userId);
}
