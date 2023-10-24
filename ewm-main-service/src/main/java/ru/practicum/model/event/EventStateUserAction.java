package ru.practicum.model.event;

import lombok.Getter;

@Getter
public enum EventStateUserAction {
    SEND_TO_REVIEW("Отправить на модерацию"),
    CANCEL_REVIEW("Отменить событие");

    private final String name;

    EventStateUserAction(String name) {
        this.name = name;
    }
}