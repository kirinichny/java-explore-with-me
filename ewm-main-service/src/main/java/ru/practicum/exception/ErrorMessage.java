package ru.practicum.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    USER_NOT_FOUND("Пользователь #%d не найден."),
    EVENT_NOT_FOUND("Событие #%d не найдено."),
    USER_EVENT_NOT_FOUND("Событие #%d у пользователя #%d не найдено."),
    USER_REQUEST_NOT_FOUND("Заявку на участие в событии #%d у пользователя #%d не найдена."),
    EVENT_CANNOT_BE_EDITED("Невозможно изменить событие, так как событие %s"),
    CATEGORY_NOT_FOUND("Категория #%d не найдена."),
    SOME_EVENTS_NOT_FOUND("Одно или несколько событий не найдено."),
    SOME_REQUESTS_NOT_FOUND("Одна или несколько заявок на участие в событии не найдены."),
    COMPILATION_NOT_FOUND("Подборка событий #%d не найдена."),
    CANNOT_APPLY_FOR_EVENT("Невозможно подать заявку на участие в событии #%d, так как событие %s."),
    PARTICIPANT_LIMIT_REACHED("Достигнут лимит участников для события #%d. Максимальный количество участников: #%d."),
    USER_SELF_REQUEST("Пользователь не может отправлять запросы на участие в своём событии."),
    INVALID_REQUEST_STATUS("Недопустимое состояние запроса на участие"),
    DATE_RANGE_INCORRECT("Временной диапазон некорректен. Конечная дата не может быть раньше начальной даты."),
    UNSUPPORTED_EVENT_SORT_OPTION("Выбранная опция сортировки событий не поддерживается");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String formatted(Object... args) {
        return String.format(message, args);
    }
}