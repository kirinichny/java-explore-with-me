package ru.practicum.model.event;

import lombok.Getter;

@Getter
public enum EventSortOption {
    EVENT_DATE("по дате события"),
    VIEWS("по количеству просмотров");

    private final String name;

    EventSortOption(String name) {
        this.name = name;
    }
}