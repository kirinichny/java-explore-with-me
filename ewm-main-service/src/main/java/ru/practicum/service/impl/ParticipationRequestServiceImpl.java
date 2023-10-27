package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusDto;
import ru.practicum.dto.participationRequest.ParticipationRequestChangeStatusResponseDto;
import ru.practicum.dto.participationRequest.ParticipationRequestResponseDto;
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
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public List<ParticipationRequestResponseDto> getRequestsByEventId(long eventId, long userId) {
        log.debug("+ getParticipationRequestsEventById: eventId={}, userId={}", eventId, userId);
        List<ParticipationRequestResponseDto> requests = requestMapper.toResponseDto(
                requestRepository.findByEventIdAndEventInitiatorId(eventId, userId));
        log.debug("- getParticipationRequestsEventById: {}", requests);
        return requests;
    }

    @Override
    public List<ParticipationRequestResponseDto> getRequestsByRequesterId(long requesterId) {
        log.debug("+ getRequestsByRequesterId: requesterId={}", requesterId);
        List<ParticipationRequestResponseDto> requests = requestMapper.toResponseDto(requestRepository.findByRequesterId(requesterId));
        log.debug("- getRequestsByRequesterId: {}", requests);
        return requests;
    }

    @Transactional
    @Override
    public ParticipationRequestResponseDto createRequest(long eventId, long userId) {
        log.debug("+ createRequest");

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Событие #%d не найдено.", eventId)));

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь #%d не найден.", userId)));

        Long eventInitiatorId = event.getInitiator().getId();
        EventState eventState = event.getState();
        Integer participantLimit = event.getParticipantLimit();
        boolean hasUnlimitedRequests = participantLimit.equals(0);
        LocalDateTime currentDate = LocalDateTime.now();

        if (eventInitiatorId.equals(userId)) {
            throw new DataIntegrityViolationException("Пользователь не может отправлять запросы" +
                    " на участие в своём событии.");
        }

        if (!eventState.equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException(
                    String.format("Невозможно подать заявку на участие в событии #%d, так как событие %s.",
                            eventId, eventState.getName()));
        }

        if (!hasUnlimitedRequests && event.getConfirmedRequestsCount() >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException(
                    String.format("Достигнут лимит участников для события #%d. Максимальный количество" +
                            " участников: #%d.", eventId, participantLimit));
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

        ParticipationRequestResponseDto createdRequest = requestMapper.toResponseDto(request);

        log.debug("- createRequest: {}", createdRequest);

        return createdRequest;
    }

    @Transactional
    @Override
    public ParticipationRequestChangeStatusResponseDto updateRequestStatuses(
            ParticipationRequestChangeStatusDto statusData, long eventId, long userId) {
        log.debug("+ updateRequestStatuses: eventId={}", eventId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие #%d у пользователя #%d не найдено.", eventId, userId)));

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
            throw new NotFoundException("Одна или несколько заявок на участие в событии не найдены.");
        }

        if (!areAllRequestsPending) {
            throw new DataIntegrityViolationException("Недопустимое состояние запроса на участие");
        }

        if (!hasUnlimitedRequests && participantLimit - confirmedRequestsCount < expectedRemainingLimit) {
            throw new DataIntegrityViolationException(String.format("Достигнут лимит участников для события #%d." +
                    " Максимальный количество участников: #%d.", eventId, participantLimit));
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

        ParticipationRequestChangeStatusResponseDto updatedRequest = ParticipationRequestChangeStatusResponseDto.builder()
                .confirmedRequests(requestMapper.toResponseDto(confirmedRequests))
                .rejectedRequests(requestMapper.toResponseDto(rejectedRequests))
                .build();

        log.debug("- updateRequestStatuses");

        return updatedRequest;
    }

    @Transactional
    @Override
    public ParticipationRequestResponseDto cancelRequest(long requestId, long userId) {
        log.debug("+ cancelRequest: requestId={}", requestId);

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Заявку на участие в событии #%d у пользователя #%d не найдена.",
                                requestId, userId)));

        request.setStatus(ParticipationRequestStatus.CANCELED);

        requestRepository.save(request);
        eventRepository.updateConfirmedRequestCountForEvents(request.getEvent().getId());

        ParticipationRequestResponseDto result = requestMapper.toResponseDto(request);

        log.debug("- cancelRequest");

        return result;
    }
}
