package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, " +
            "case when (?3 = true) then count(distinct eh.ip) else count(*) end) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= ?1 and eh.timestamp <= ?2 " +
            "group by eh.app, eh.uri " +
            "order by count(*) desc")
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, boolean onlyUniqueIp);

    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, " +
            "case when (?4 = true) then count(distinct eh.ip) else count(*) end) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= ?1 and eh.timestamp <= ?2 " +
            "and eh.uri in ?3 " +
            "group by eh.app, eh.uri " +
            "order by count(*) desc")
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean onlyUniqueIp);
}