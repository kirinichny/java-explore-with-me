package ru.practicum.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.constants.DateTimeFormatConstants;
import ru.practicum.service.StatsClientService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClientServiceImpl implements StatsClientService {
    private final StatsClient statsClient;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean onlyUniqueHits) {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormatConstants.STANDARD);

        String startDate = start.format(formatter);
        String endDate = end.format(formatter);

        ResponseEntity<Object> response = statsClient.getStats(startDate, endDate, uris, onlyUniqueHits);

        ObjectMapper objectMapper = new ObjectMapper();
        List<ViewStatsDto> result = new ArrayList<>();

        if (response.getBody() instanceof List) {
            for (Object item : (List<?>) response.getBody()) {
                if (item instanceof HashMap) {
                    ViewStatsDto viewStats = objectMapper.convertValue(item, ViewStatsDto.class);
                    result.add(viewStats);
                }
            }
        }

        return result;
    }

    @Override
    public void addHit(HttpServletRequest request) {
        String currentDateTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(DateTimeFormatConstants.STANDARD));

        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(currentDateTime)
                .build();

        statsClient.addHit(endpointHit);
    }
}