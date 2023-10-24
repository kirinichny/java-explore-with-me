package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.UserCreateOrUpdateDto;
import ru.practicum.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers(List<Long> userIds, Pageable pageable);

    UserResponseDto createUser(UserCreateOrUpdateDto user);

    void deleteUser(long userId);
}
