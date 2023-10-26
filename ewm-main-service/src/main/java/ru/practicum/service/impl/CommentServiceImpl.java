package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
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
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto getPublishedCommentById(long commentId, long eventId) {
        return commentMapper.toCommentResponseDto(getPublishedCommentByIdOrThrow(commentId));
    }

    @Override
    public CommentResponseDto getCommentById(long commentId, long authorId) {
        return commentMapper.toCommentResponseDto(getCommentByIdOrThrow(commentId, authorId));
    }

    @Override
    public List<CommentResponseDto> searchComments(CommentSearchPublicFilter filter, long eventId, Pageable pageable) {
        Specification<Comment> spec = Specification
                .where(CommentSpecification.withCommentText(filter.getText()))
                .and(CommentSpecification.withRangeStart(filter.getRangeStart()))
                .and(CommentSpecification.withRangeEnd(filter.getRangeEnd()))
                .and(CommentSpecification.hasParticipated(filter.getHasParticipated()))
                .and(CommentSpecification.withEventId(eventId))
                .and(CommentSpecification.withStatus(CommentStatus.PUBLISHED));

        List<Comment> comments = commentRepository.findAll(spec, pageable).getContent();

        return commentMapper.toCommentResponseDto(comments);
    }

    @Override
    public List<CommentResponseDto> searchComments(CommentSearchPrivateFilter filter, long authorId, Pageable pageable) {
        CommentStatus commentStatus = (filter.getShowDeleted()) ?
                CommentStatus.DELETED_BY_AUTHOR : CommentStatus.PUBLISHED;

        Specification<Comment> spec = Specification
                .where(CommentSpecification.withCommentText(filter.getText()))
                .and(CommentSpecification.withRangeStart(filter.getRangeStart()))
                .and(CommentSpecification.withRangeEnd(filter.getRangeEnd()))
                .and(CommentSpecification.hasParticipated(filter.getHasParticipated()))
                .and(CommentSpecification.withAuthorId(authorId))
                .and(CommentSpecification.withStatus(commentStatus));

        List<Comment> comments = commentRepository.findAll(spec, pageable).getContent();

        return commentMapper.toCommentResponseDto(comments);
    }

    @Override
    public List<CommentResponseDto> searchComments(CommentSearchAdminFilter filter, Pageable pageable) {
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

        List<Comment> comments = commentRepository.findAll(spec, pageable).getContent();

        return commentMapper.toCommentResponseDto(comments);
    }

    @Override
    public CommentResponseDto createComment(CommentCreateDto comment, Long authorId) {
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

        return commentMapper.toCommentResponseDto(commentRepository.save(commentToSave));
    }

    @Override
    public CommentResponseDto createComment(CommentCreateDto comment) {
        return createComment(comment, null);
    }

    @Override
    public CommentResponseDto updateComment(long commentId, CommentUpdateDto comment, long userId) {
        Comment currentComment = getCommentByIdOrThrow(commentId, userId);


        currentComment.setComment(comment.getComment());
        currentComment.setLastModifiedDate(LocalDateTime.now());

        return commentMapper.toCommentResponseDto(commentRepository.save(currentComment));
    }

    @Transactional
    @Override
    public void deleteComment(long commentId, long userId) {
        getCommentByIdOrThrow(commentId, userId);
        commentRepository.updateCommentStatus(commentId, CommentStatus.DELETED_BY_AUTHOR);
    }

    @Transactional
    @Override
    public void deleteComment(long commentId) {
        getPublishedCommentByIdOrThrow(commentId);
        commentRepository.updateCommentStatus(commentId, CommentStatus.DELETED_BY_ADMIN);
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
