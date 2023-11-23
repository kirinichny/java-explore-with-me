package ru.practicum.model.comment;

import lombok.Getter;

@Getter
public enum CommentStatus {
    PUBLISHED("опубликован"),
    DELETED_BY_AUTHOR("удален автором"),
    DELETED_BY_ADMIN("удален администратором");

    private final String name;

    CommentStatus(String name) {
        this.name = name;
    }
}