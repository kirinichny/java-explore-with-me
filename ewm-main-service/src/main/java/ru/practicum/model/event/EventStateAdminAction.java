package ru.practicum.model.event;

import lombok.Getter;

@Getter
public enum EventStateAdminAction {
    PUBLISH_EVENT("Опубликовать событие"),
    REJECT_EVENT("Отклонить событие");

    private final String name;

    EventStateAdminAction(String name) {
        this.name = name;
    }
}