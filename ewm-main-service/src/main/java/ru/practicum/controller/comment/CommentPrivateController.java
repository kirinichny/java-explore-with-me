package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.dto.comment.CommentUpdateDto;
import ru.practicum.model.comment.CommentSearchPrivateFilter;
import ru.practicum.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentResponseDto getCommentById(@PathVariable long commentId, @PathVariable long userId) {
        log.debug("+ getCommentById: commentId={}", commentId);
        CommentResponseDto comment = commentService.getCommentById(commentId, userId);
        log.debug("- getCommentById: {}", comment);
        return comment;
    }

    @GetMapping
    public List<CommentResponseDto> searchComments(
            @ModelAttribute CommentSearchPrivateFilter filter,
            @PathVariable long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size
    ) {
        log.debug("+ searchComments");
        Pageable pageable = PageRequest.of(from / size, size);
        List<CommentResponseDto> comments = commentService.searchComments(filter, userId, pageable);
        log.debug("- searchComments: {}", comments);
        return comments;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@RequestBody @Valid CommentCreateDto comment, @PathVariable long userId) {
        log.debug("+ createComment: comment={}", comment);
        CommentResponseDto createdComment = commentService.createComment(comment, userId);
        log.debug("- createComment");
        return createdComment;
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable long commentId,
                                            @RequestBody @Valid CommentUpdateDto comment,
                                            @PathVariable long userId) {
        log.debug("+ updateComment: commentId={}", commentId);
        CommentResponseDto updatedComment = commentService.updateComment(commentId, comment, userId);
        log.debug("- updateComment: {}", updatedComment);
        return updatedComment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId, @PathVariable long userId) {
        log.debug("+ deleteComment: commentId={}", commentId);
        commentService.deleteComment(commentId, userId);
        log.debug("- deleteComment");
    }
}