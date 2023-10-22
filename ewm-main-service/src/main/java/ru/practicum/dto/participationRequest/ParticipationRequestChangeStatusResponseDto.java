package ru.practicum.dto.participationRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ParticipationRequestChangeStatusResponseDto {
    private List<ParticipationRequestResponseDto> confirmedRequests;
    private List<ParticipationRequestResponseDto> rejectedRequests;
}
