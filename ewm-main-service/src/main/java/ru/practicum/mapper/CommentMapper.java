package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.user.User;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring",
        uses = {EventMapper.class, UserMapper.class})
public interface CommentMapper {
    @Mapping(source = "isAnonymous", target = "isAnonymous", defaultExpression = "java(Boolean.FALSE)")
    @Mapping(target = "hasParticipated", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    Comment toComment(CommentCreateDto comment);

    @Mapping(source = "parentComment.id", target = "parentComment.id")
    @Mapping(source = "parentComment.comment", target = "parentComment.comment")
    @Mapping(target = "author", expression = "java(hideAuthor(comment))")
    CommentResponseDto toCommentResponseDto(Comment comment);

    List<CommentResponseDto> toCommentResponseDto(List<Comment> events);

    default UserShortDto hideAuthor(final Comment comment) {
        User author = comment.getAuthor();

        return Objects.nonNull(author) && !comment.getIsAnonymous() ?
                new UserShortDto(author.getId(), author.getName()) : null;
    }
}