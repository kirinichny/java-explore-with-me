package ru.practicum.service;

import ru.practicum.dto.user.UserCreateOrUpdateDto;
import ru.practicum.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers(List<Long> userIds, Integer from, Integer size);

    UserResponseDto createUser(UserCreateOrUpdateDto user);

    void deleteUser(long userId);
}
