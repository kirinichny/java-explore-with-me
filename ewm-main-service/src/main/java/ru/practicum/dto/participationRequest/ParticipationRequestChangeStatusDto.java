package ru.practicum.dto.participationRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.model.participationRequest.ParticipationRequestStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ParticipationRequestChangeStatusDto {
    @NotNull
    @Size(min = 1)
    private List<Long> requestIds;

    @NotNull
    private ParticipationRequestStatus status;
}
