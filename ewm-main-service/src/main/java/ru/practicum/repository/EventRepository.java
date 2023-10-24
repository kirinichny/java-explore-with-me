package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    Optional<Event> findByIdAndState(Long eventId, EventState eventState);

    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    boolean existsAllByIdIn(List<Long> eventIds);

    @Modifying
    @Query("UPDATE Event evt SET evt.confirmedRequestsCount =" +
            " (SELECT count(pr)" +
            " FROM ParticipationRequest pr WHERE pr.event.id = evt.id AND pr.status = 'CONFIRMED')" +
            " WHERE evt.id = :eventId")
    void updateConfirmedRequestCountForEvents(@Param("eventId") Long eventId);
}