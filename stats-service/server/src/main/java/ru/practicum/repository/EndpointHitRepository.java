package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, " +
            "case when (:onlyUniqueIp = true) then count(distinct eh.ip) else count(*) end) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= :start and eh.timestamp <= :end " +
            "group by eh.app, eh.uri " +
            "order by count(*) desc")
    List<ViewStatsDto> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("onlyUniqueIp") boolean onlyUniqueIp);

    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, " +
            "case when (:onlyUniqueIp = true) then count(distinct eh.ip) else count(*) end) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= :start and eh.timestamp <= :end " +
            "and eh.uri in :uris " +
            "group by eh.app, eh.uri " +
            "order by count(*) desc")
    List<ViewStatsDto> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris,
            @Param("onlyUniqueIp") boolean onlyUniqueIp);
}