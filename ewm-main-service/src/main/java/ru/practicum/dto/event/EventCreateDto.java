package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class EventCreateDto extends EventCreateOrUpdateBaseDto {
}