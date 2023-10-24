package ru.practicum.service;

import ru.practicum.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsClientService {
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                List<String> uris, boolean onlyUniqueHits);

    void addHit(HttpServletRequest request);
}