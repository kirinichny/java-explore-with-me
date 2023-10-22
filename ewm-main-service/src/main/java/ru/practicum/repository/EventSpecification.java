package ru.practicum.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;

public class EventSpecification {
    public static Specification<Event> withRangeStart(LocalDateTime rangeStart) {
        return (root, query, builder) ->
                rangeStart == null ? builder.conjunction() :
                        builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
    }

    public static Specification<Event> withRangeEnd(LocalDateTime rangeEnd) {
        return (root, query, builder) ->
                rangeEnd == null ? builder.conjunction() :
                        builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
    }

    public static Specification<Event> withCategories(List<Long> categories) {
        return (root, query, builder) ->
                categories == null || categories.isEmpty() ? builder.conjunction() :
                        root.get("category").get("id").in(categories);
    }

    public static Specification<Event> withAnnotation(String text) {
        return (root, query, builder) ->
                text == null || text.isBlank() ? builder.conjunction() :
                        builder.like(
                                builder.lower(root.get("annotation")),
                                String.format("%%%s%%", text.toLowerCase()));
    }

    public static Specification<Event> withDescription(String text) {
        return (root, query, builder) ->
                text == null || text.isBlank() ? builder.conjunction() :
                        builder.like(
                                builder.lower(root.get("description")),
                                String.format("%%%s%%", text.toLowerCase()));
    }

    public static Specification<Event> withInitiatorIds(List<Long> initiatorIds) {
        return (root, query, builder) ->
                initiatorIds == null || initiatorIds.isEmpty() ? builder.conjunction() :
                        root.get("initiator").get("id").in(initiatorIds);
    }

    public static Specification<Event> withPaymentRequired(Boolean isPaymentRequired) {
        return (root, query, builder) ->
                isPaymentRequired == null ? builder.conjunction() :
                        builder.equal(root.get("isPaymentRequired"), isPaymentRequired);
    }

    public static Specification<Event> withStates(List<EventState> states) {
        return (root, query, builder) ->
                states == null || states.isEmpty() ? builder.conjunction() :
                        root.get("state").in(states);
    }

    public static Specification<Event> withState(EventState state) {
        return (root, query, builder) ->
                state == null ? builder.conjunction() :
                        builder.equal(root.get("state"), state);
    }

    public static Specification<Event> onlyAvailable(Boolean onlyAvailable) {
        return (root, query, builder) -> {
            if (onlyAvailable != null && onlyAvailable) {
                Path<Boolean> requestModerationPath = root.get("requestModeration");
                Path<Integer> participantLimitPath = root.get("participantLimit");
                Path<Integer> confirmedRequestsCountPath = root.get("confirmedRequestsCount");

                Predicate moderationIsNotRequired = builder.equal(requestModerationPath, false);
                Predicate hasEnoughPlaces = builder.greaterThan(
                        builder.diff(
                                participantLimitPath,
                                confirmedRequestsCountPath
                        ), 0);

                return builder.or(moderationIsNotRequired, hasEnoughPlaces);
            } else {
                return builder.conjunction();
            }
        };
    }
}