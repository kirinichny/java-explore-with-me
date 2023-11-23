package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.participationRequest.ParticipationRequest;
import ru.practicum.model.participationRequest.ParticipationRequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(long requesterId);

    List<ParticipationRequest> findByEventIdAndEventInitiatorId(long eventId, long eventInitiatorId);

    List<ParticipationRequest> getAllByEventIdAndStatus(long eventId, ParticipationRequestStatus status);

    List<Long> getIdsAllByEventIdAndStatus(long eventId, ParticipationRequestStatus status);

    boolean existsAllByIdInAndEventId(List<Long> requestIds, long eventId);

    boolean existsAllByIdInAndStatus(List<Long> requestIds, ParticipationRequestStatus status);

    boolean existsByEventIdAndRequesterIdAndStatus(long eventId, long requesterId, ParticipationRequestStatus status);

    @Modifying
    @Query("UPDATE ParticipationRequest pr SET pr.status = :status WHERE pr.id IN :requestIds")
    void updateRequestStatuses(@Param("requestIds") List<Long> requestIds, @Param("status") ParticipationRequestStatus status);
}