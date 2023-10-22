package ru.practicum.validation.event;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.Objects;

public class EventDateValidator implements ConstraintValidator<EventDateValidation, LocalDateTime> {
    @Override
    public void initialize(EventDateValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value)) {
            return true;
        }

        LocalDateTime nowPlus2Hours = LocalDateTime.now().plusHours(2);

        return value.isAfter(nowPlus2Hours);
    }
}