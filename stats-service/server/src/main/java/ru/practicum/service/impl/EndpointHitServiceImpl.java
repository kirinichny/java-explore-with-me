package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.repository.EndpointHitRepository;
import ru.practicum.service.EndpointHitService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean onlyUniqueHits) {
        if (end.isBefore(start)) {
            throw new ValidationException("Конечная дата должна быть после начальной даты.");
        }

        List<String> withoutBracketsUris = uris.stream()
                .map(uri -> uri.replace("[", "").replace("]", ""))
                .collect(Collectors.toList());

        return (withoutBracketsUris.isEmpty()) ? endpointHitRepository.getStats(start, end, onlyUniqueHits)
                : endpointHitRepository.getStats(start, end, withoutBracketsUris, onlyUniqueHits);
    }

    @Override
    public EndpointHitDto addHit(EndpointHitDto hit) {
        return EndpointHitMapper.toEndpointHitDto(
                endpointHitRepository.save(EndpointHitMapper.toEndpointHit(hit)));
    }
}