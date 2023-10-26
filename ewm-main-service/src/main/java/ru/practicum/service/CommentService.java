package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.dto.comment.CommentUpdateDto;
import ru.practicum.model.comment.CommentSearchAdminFilter;
import ru.practicum.model.comment.CommentSearchPrivateFilter;
import ru.practicum.model.comment.CommentSearchPublicFilter;

import java.util.List;

public interface CommentService {
    CommentResponseDto getPublishedCommentById(long commentId, long eventId);

    CommentResponseDto getCommentById(long commentId, long userId);

    List<CommentResponseDto> searchComments(CommentSearchPublicFilter filter, long eventId, Pageable pageable);

    List<CommentResponseDto> searchComments(CommentSearchPrivateFilter filter, long authorId, Pageable pageable);

    List<CommentResponseDto> searchComments(CommentSearchAdminFilter filter, Pageable pageable);

    CommentResponseDto createComment(CommentCreateDto comment);

    CommentResponseDto createComment(CommentCreateDto comment, Long authorId);

    CommentResponseDto updateComment(long commentId, CommentUpdateDto comment, long userId);

    void deleteComment(long commentId, long userId);

    void deleteComment(long commentId);
}
