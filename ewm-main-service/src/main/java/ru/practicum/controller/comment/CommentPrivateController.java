package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
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
public class CommentPrivateController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentResponseDto getCommentById(@PathVariable long commentId, @PathVariable long userId) {
        return commentService.getCommentById(commentId, userId);
    }

    @GetMapping
    public List<CommentResponseDto> searchComments(
            @ModelAttribute CommentSearchPrivateFilter filter,
            @PathVariable long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size
    ) {
        return commentService.searchComments(filter, userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@RequestBody @Valid CommentCreateDto comment, @PathVariable long userId) {
        return commentService.createComment(comment, userId);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable long commentId,
                                            @RequestBody @Valid CommentUpdateDto comment,
                                            @PathVariable long userId) {
        return commentService.updateComment(commentId, comment, userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId, @PathVariable long userId) {
        commentService.deleteComment(commentId, userId);
    }
}