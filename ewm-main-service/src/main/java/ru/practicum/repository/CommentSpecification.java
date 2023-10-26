package ru.practicum.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;

public class CommentSpecification {
    public static Specification<Comment> withRangeStart(LocalDateTime rangeStart) {
        return (root, query, builder) ->
                rangeStart == null ? builder.conjunction() :
                        builder.or(
                                builder.greaterThanOrEqualTo(root.get("lastModifiedDate"), rangeStart),
                                builder.greaterThanOrEqualTo(root.get("createdOn"), rangeStart)
                        );
    }

    public static Specification<Comment> withRangeEnd(LocalDateTime rangeEnd) {
        return (root, query, builder) ->
                rangeEnd == null ? builder.conjunction() :
                        builder.or(
                                builder.lessThanOrEqualTo(root.get("lastModifiedDate"), rangeEnd),
                                builder.lessThanOrEqualTo(root.get("createdOn"), rangeEnd)
                        );
    }

    public static Specification<Comment> withCommentText(String text) {
        return (root, query, builder) ->
                text == null || text.isBlank() ? builder.conjunction() :
                        builder.like(
                                builder.lower(root.get("comment")),
                                String.format("%%%s%%", text.toLowerCase()));
    }

    public static Specification<Comment> withAuthorIds(List<Long> authorIds) {
        return (root, query, builder) ->
                authorIds == null || authorIds.isEmpty() ? builder.conjunction() :
                        root.get("author").get("id").in(authorIds);
    }

    public static Specification<Comment> withAuthorId(Long authorId) {
        return (root, query, builder) ->
                authorId == null ? builder.conjunction() :
                        builder.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Comment> withEventId(Long eventId) {
        return (root, query, builder) ->
                eventId == null ? builder.conjunction() :
                        builder.equal(root.get("event").get("id"), eventId);
    }

    public static Specification<Comment> hasParticipated(Boolean hasParticipated) {
        return (root, query, builder) ->
                hasParticipated == null || !hasParticipated ? builder.conjunction() :
                        builder.isTrue(root.get("hasParticipated"));
    }

    public static Specification<Comment> onlyAnonymous(Boolean onlyAnonymous) {
        return (root, query, builder) ->
                onlyAnonymous == null || !onlyAnonymous ? builder.conjunction() :
                        builder.or(
                                builder.isTrue(root.get("isAnonymous")),
                                builder.isNull(root.get("author"))
                        );
    }

    public static Specification<Comment> withStatus(CommentStatus status) {
        return (root, query, builder) ->
                status == null ? builder.conjunction() :
                        builder.equal(root.get("status"), status);
    }
}