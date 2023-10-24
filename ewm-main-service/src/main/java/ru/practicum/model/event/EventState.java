package ru.practicum.model.event;

import lombok.Getter;

@Getter
public enum EventState {
    PENDING("ожидает публикации"),
    PUBLISHED("опубликовано"),
    CANCELED("отменено");

    private final String name;

    EventState(String name) {
        this.name = name;
    }
}