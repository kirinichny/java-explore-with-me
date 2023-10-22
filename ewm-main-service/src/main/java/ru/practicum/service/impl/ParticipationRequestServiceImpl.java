package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusDto;
import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusResponseDto;
import ru.practicum.dto.participationRequest.ParticipationRequestResponseDto;
import ru.practicum.exception.ErrorMessage;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.participationRequest.ParticipationRequest;
import ru.practicum.model.participationRequest.ParticipationRequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.ParticipationRequestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public List<ParticipationRequestResponseDto> getRequestsByEventId(long eventId, long userId) {
        List<ParticipationRequest> requests = requestRepository.findByEventIdAndEventInitiatorId(eventId, userId);

        return requestMapper.toResponseDto(requests);
    }

    @Override
    public List<ParticipationRequestResponseDto> getRequestsByRequesterId(long requesterId) {
        List<ParticipationRequest> requests = requestRepository.findByRequesterId(requesterId);

        return requestMapper.toResponseDto(requests);
    }

    @Transactional
    @Override
    public ParticipationRequestResponseDto createRequest(long eventId, long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                ErrorMessage.EVENT_NOT_FOUND.formatted(eventId)));

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                ErrorMessage.USER_NOT_FOUND.formatted(userId)));

        Long eventInitiatorId = event.getInitiator().getId();
        EventState eventState = event.getState();
        Integer participantLimit = event.getParticipantLimit();
        boolean hasUnlimitedRequests = participantLimit.equals(0);
        LocalDateTime currentDate = LocalDateTime.now();

        if (eventInitiatorId.equals(userId)) {
            throw new DataIntegrityViolationException(
                    ErrorMessage.USER_SELF_REQUEST.formatted());
        }

        if (!eventState.equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException(
                    ErrorMessage.CANNOT_APPLY_FOR_EVENT.formatted(eventId, eventState.getName()));
        }

        if (!hasUnlimitedRequests && event.getConfirmedRequestsCount() >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException(
                    ErrorMessage.PARTICIPANT_LIMIT_REACHED.formatted(eventId, participantLimit));
        }

        ParticipationRequestStatus requestStatus = (hasUnlimitedRequests || !event.getRequestModeration())
                ? ParticipationRequestStatus.CONFIRMED : ParticipationRequestStatus.PENDING;

        ParticipationRequest request = requestRepository.save(ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .status(requestStatus)
                .createdOn(currentDate)
                .build());

        eventRepository.updateConfirmedRequestCountForEvents(eventId);

        return requestMapper.toResponseDto(request);
    }

    @Transactional
    @Override
    public ParticipationRequestChangeStatusResponseDto updateRequestStatuses(
            ParticipationRequestChangeStatusDto statusData, long eventId, long userId) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorMessage.USER_EVENT_NOT_FOUND.formatted(eventId, userId)));

        List<Long> requestIds = statusData.getRequestIds();
        ParticipationRequestStatus newStatus = statusData.getStatus();
        int participantLimit = event.getParticipantLimit();
        boolean hasUnlimitedRequests = participantLimit == 0;
        int confirmedRequestsCount = event.getConfirmedRequestsCount();
        int expectedRemainingLimit = (newStatus.equals(ParticipationRequestStatus.CONFIRMED)) ?
                event.getConfirmedRequestsCount() : 0;

        boolean areAllRequestsPending = requestRepository
                .existsAllByIdInAndStatus(requestIds, ParticipationRequestStatus.PENDING);

        boolean isRequestsExist = requestRepository.existsAllByIdInAndEventId(requestIds, eventId);

        if (!isRequestsExist) {
            throw new NotFoundException(ErrorMessage.SOME_REQUESTS_NOT_FOUND.formatted());
        }

        if (!areAllRequestsPending) {
            throw new DataIntegrityViolationException(
                    ErrorMessage.INVALID_REQUEST_STATUS.formatted());
        }

        if (!hasUnlimitedRequests && participantLimit - confirmedRequestsCount < expectedRemainingLimit) {
            throw new DataIntegrityViolationException(
                    ErrorMessage.PARTICIPANT_LIMIT_REACHED.formatted(eventId, participantLimit));
        }

        requestRepository.updateRequestStatuses(requestIds, newStatus);

        if (!hasUnlimitedRequests && participantLimit - confirmedRequestsCount - expectedRemainingLimit <= 0) {
            requestRepository.updateRequestStatuses(
                    requestRepository.getIdsAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING),
                    ParticipationRequestStatus.REJECTED);
        }

        eventRepository.updateConfirmedRequestCountForEvents(eventId);

        List<ParticipationRequest> confirmedRequests = requestRepository
                .getAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);

        List<ParticipationRequest> rejectedRequests = requestRepository
                .getAllByEventIdAndStatus(eventId, ParticipationRequestStatus.REJECTED);

        return ParticipationRequestChangeStatusResponseDto.builder()
                .confirmedRequests(requestMapper.toResponseDto(confirmedRequests))
                .rejectedRequests(requestMapper.toResponseDto(rejectedRequests))
                .build();
    }

    @Transactional
    @Override
    public ParticipationRequestResponseDto cancelRequest(long requestId, long userId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorMessage.USER_REQUEST_NOT_FOUND.formatted(requestId, userId)));

        request.setStatus(ParticipationRequestStatus.CANCELED);

        requestRepository.save(request);
        eventRepository.updateConfirmedRequestCountForEvents(request.getEvent().getId());

        return requestMapper.toResponseDto(request);
    }

}
