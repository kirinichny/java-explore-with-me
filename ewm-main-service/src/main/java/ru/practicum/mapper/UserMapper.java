package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.user.UserCreateOrUpdateDto;
import ru.practicum.dto.user.UserResponseDto;
import ru.practicum.model.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateOrUpdateDto user);

    UserResponseDto toUserResponseDto(User user);

    List<UserResponseDto> toUserResponseDto(List<User> users);
}
