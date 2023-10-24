package ru.practicum.model.participationRequest;

import lombok.Getter;

@Getter
public enum ParticipationRequestStatus {
    PENDING("ожидает подтверждения"),
    CONFIRMED("подтвержден"),
    REJECTED("отклонен модератором"),
    CANCELED("отменен пользователем");

    private final String name;

    ParticipationRequestStatus(String name) {
        this.name = name;
    }
}