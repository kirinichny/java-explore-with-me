package ru.practicum.controller.participationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.participationRequest.ParticipationRequestResponseDto;
import ru.practicum.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ParticipationRequestPrivateController {
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<ParticipationRequestResponseDto> getRequestsByRequesterId(
            @PathVariable(name = "userId") long requesterId) {
        log.debug("+ getRequestsByRequesterId: requesterId={}", requesterId);
        List<ParticipationRequestResponseDto> requests = requestService.getRequestsByRequesterId(requesterId);
        log.debug("- getRequestsByRequesterId: {}", requests);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestResponseDto createRequest(@RequestParam long eventId,
                                                         @PathVariable long userId) {
        log.debug("+ createRequest");
        ParticipationRequestResponseDto createdRequest = requestService.createRequest(eventId, userId);
        log.debug("- createRequest: {}", createdRequest);
        return createdRequest;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestResponseDto cancelRequest(@PathVariable long requestId, @PathVariable long userId) {
        log.debug("+ cancelRequest: requestId={}", requestId);
        ParticipationRequestResponseDto request = requestService.cancelRequest(requestId, userId);
        log.debug("- cancelRequest");
        return request;
    }
}
