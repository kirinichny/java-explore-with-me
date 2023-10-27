package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.dto.comment.CommentUpdateDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentSearchAdminFilter;
import ru.practicum.model.comment.CommentSearchPrivateFilter;
import ru.practicum.model.comment.CommentSearchPublicFilter;
import ru.practicum.model.comment.CommentStatus;
import ru.practicum.model.event.Event;
import ru.practicum.model.participationRequest.ParticipationRequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.CommentSpecification;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto getPublishedCommentById(long commentId, long eventId) {
        log.debug("+ getCommentById: commentId={}", commentId);
        CommentResponseDto comment = commentMapper.toCommentResponseDto(getPublishedCommentByIdOrThrow(commentId));
        log.debug("- getCommentById: {}", comment);
        return comment;
    }

    @Override
    public CommentResponseDto getCommentById(long commentId, long authorId) {
        log.debug("+ getCommentById: commentId={}", commentId);
        CommentResponseDto comment = commentMapper.toCommentResponseDto(getCommentByIdOrThrow(commentId, authorId));
        log.debug("- getCommentById: {}", comment);
        return comment;
    }

    @Override
    public List<CommentResponseDto> searchComments(CommentSearchPublicFilter filter, long eventId, Integer from, Integer size) {
        log.debug("+ searchComments");

        Pageable pageable = PageRequest.of(from / size, size);

        Specification<Comment> spec = Specification
                .where(CommentSpecification.withCommentText(filter.getText()))
                .and(CommentSpecification.withRangeStart(filter.getRangeStart()))
                .and(CommentSpecification.withRangeEnd(filter.getRangeEnd()))
                .and(CommentSpecification.hasParticipated(filter.getHasParticipated()))
                .and(CommentSpecification.withEventId(eventId))
                .and(CommentSpecification.withStatus(CommentStatus.PUBLISHED));

        List<CommentResponseDto> comments = commentMapper.toCommentResponseDto(
                commentRepository.findAll(spec, pageable).getContent());

        log.debug("- searchComments: {}", comments);

        return comments;
    }

    @Override
    public List<CommentResponseDto> searchComments(CommentSearchPrivateFilter filter, long authorId, Integer from, Integer size) {
        log.debug("+ searchComments");

        Pageable pageable = PageRequest.of(from / size, size);

        CommentStatus commentStatus = (filter.getShowDeleted()) ?
                CommentStatus.DELETED_BY_AUTHOR : CommentStatus.PUBLISHED;

        Specification<Comment> spec = Specification
                .where(CommentSpecification.withCommentText(filter.getText()))
                .and(CommentSpecification.withRangeStart(filter.getRangeStart()))
                .and(CommentSpecification.withRangeEnd(filter.getRangeEnd()))
                .and(CommentSpecification.hasParticipated(filter.getHasParticipated()))
                .and(CommentSpecification.withAuthorId(authorId))
                .and(CommentSpecification.withStatus(commentStatus));

        List<CommentResponseDto> comments = commentMapper.toCommentResponseDto(
                commentRepository.findAll(spec, pageable).getContent());

        log.debug("- searchComments: {}", comments);

        return comments;
    }

    @Override
    public List<CommentResponseDto> searchComments(CommentSearchAdminFilter filter, Integer from, Integer size) {
        log.debug("+ searchComments");

        Pageable pageable = PageRequest.of(from / size, size);

        CommentStatus commentStatus = (filter.getShowDeleted()) ?
                CommentStatus.DELETED_BY_ADMIN : CommentStatus.PUBLISHED;

        Specification<Comment> spec = Specification
                .where(CommentSpecification.withCommentText(filter.getText()))
                .and(CommentSpecification.withRangeStart(filter.getRangeStart()))
                .and(CommentSpecification.withRangeEnd(filter.getRangeEnd()))
                .and(CommentSpecification.hasParticipated(filter.getHasParticipated()))
                .and(CommentSpecification.withAuthorIds(filter.getAuthorIds()))
                .and(CommentSpecification.onlyAnonymous(filter.getOnlyAnonymous()))
                .and(CommentSpecification.withStatus(commentStatus));

        List<CommentResponseDto> comments = commentMapper.toCommentResponseDto(
                commentRepository.findAll(spec, pageable).getContent());

        log.debug("- searchComments: {}", comments);

        return comments;
    }

    @Override
    public CommentResponseDto createComment(CommentCreateDto comment, Long authorId) {
        log.debug("+ createComment: comment={}", comment);

        Event event = getEventByIdOrThrow(comment.getEventId());
        Long parentCommentId = comment.getParentCommentId();

        User author = Objects.nonNull(authorId) ?
                getUserByIdOrThrow(authorId) : null;

        Comment parentComment = Objects.nonNull(parentCommentId) ?
                getPublishedCommentByIdOrThrow(parentCommentId) : null;

        boolean hasParticipated = Objects.nonNull(authorId) && participationRequestRepository
                .existsByEventIdAndRequesterIdAndStatus(comment.getEventId(), authorId,
                        ParticipationRequestStatus.CONFIRMED);

        LocalDateTime currentDate = LocalDateTime.now();
        Comment commentToSave = commentMapper.toComment(comment);

        commentToSave.setEvent(event);
        commentToSave.setAuthor(author);
        commentToSave.setParentComment(parentComment);
        commentToSave.setHasParticipated(hasParticipated);
        commentToSave.setStatus(CommentStatus.PUBLISHED);
        commentToSave.setCreatedOn(currentDate);

        CommentResponseDto createdComment = commentMapper.toCommentResponseDto(commentRepository.save(commentToSave));

        log.debug("- createComment");

        return createdComment;
    }

    @Override
    public CommentResponseDto createComment(CommentCreateDto comment) {
        return createComment(comment, null);
    }

    @Override
    public CommentResponseDto updateComment(long commentId, CommentUpdateDto comment, long userId) {
        log.debug("+ updateComment: commentId={}", commentId);

        Comment currentComment = getCommentByIdOrThrow(commentId, userId);

        currentComment.setComment(comment.getComment());
        currentComment.setLastModifiedDate(LocalDateTime.now());

        CommentResponseDto updatedComment = commentMapper.toCommentResponseDto(commentRepository.save(currentComment));

        log.debug("- updateComment: {}", updatedComment);

        return updatedComment;
    }

    @Transactional
    @Override
    public void deleteComment(long commentId, long userId) {
        log.debug("+ deleteComment: commentId={}", commentId);
        getCommentByIdOrThrow(commentId, userId);
        commentRepository.updateCommentStatus(commentId, CommentStatus.DELETED_BY_AUTHOR);
        log.debug("- deleteComment");
    }

    @Transactional
    @Override
    public void deleteComment(long commentId) {
        log.debug("+ deleteComment: commentId={}", commentId);
        getPublishedCommentByIdOrThrow(commentId);
        commentRepository.updateCommentStatus(commentId, CommentStatus.DELETED_BY_ADMIN);
        log.debug("- deleteComment");
    }

    private Comment getPublishedCommentByIdOrThrow(long commentId) throws NotFoundException {
        return commentRepository.findByIdAndStatus(commentId, CommentStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий #%d не найден.", commentId)));
    }

    private Comment getCommentByIdOrThrow(Long commentId, Long authorId) {
        return commentRepository.findByIdAndAuthorIdAndStatus(commentId, authorId, CommentStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Комментарий #%d у пользователя #%d не найден.", commentId, authorId)));
    }

    private User getUserByIdOrThrow(long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь #%d не найден.", userId)));
    }

    private Event getEventByIdOrThrow(long eventId) throws NotFoundException {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие #%d не найдено.", eventId)));
    }
}
