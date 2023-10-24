package ru.practicum.dto.participationRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.constants.DateTimeFormatConstants;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ParticipationRequestResponseDto {
    private long id;
    private long event;
    private long requester;
    private String status;

    @JsonFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime created;
}