package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CompilationResponseDto {
    private Long id;
    private String title;
    private List<EventShortDto> events;
    private boolean pinned;
}
