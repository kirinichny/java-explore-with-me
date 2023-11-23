package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.constants.DateTimeFormatConstants;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.comment.CommentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private EventShortDto event;
    private UserShortDto author;
    private ParentCommentDto parentComment;
    private Boolean isAnonymous;
    private Boolean hasParticipated;
    private String comment;
    private CommentStatus status;

    @JsonFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime lastModifiedDate;

    @JsonFormat(pattern = DateTimeFormatConstants.STANDARD)
    private LocalDateTime createdOn;
}