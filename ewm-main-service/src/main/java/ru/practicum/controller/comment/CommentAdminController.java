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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.model.comment.CommentSearchAdminFilter;
import ru.practicum.service.CommentService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentResponseDto> searchComments(
            @ModelAttribute CommentSearchAdminFilter filter,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size
    ) {
        log.debug("+ searchComments");
        Pageable pageable = PageRequest.of(from / size, size);
        List<CommentResponseDto> comments = commentService.searchComments(filter, pageable);
        log.debug("- searchComments: {}", comments);
        return comments;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        log.debug("+ deleteComment: commentId={}", commentId);
        commentService.deleteComment(commentId);
        log.debug("- deleteComment");
    }
}