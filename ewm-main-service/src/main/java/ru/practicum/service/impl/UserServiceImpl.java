package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserCreateOrUpdateDto;
import ru.practicum.dto.user.UserResponseDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.user.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        log.debug("+ getUsers");

        Pageable pageable = PageRequest.of(from / size, size);

        List<User> users = (Objects.isNull(userIds) || userIds.isEmpty())
                ? userRepository.findAll(pageable).getContent()
                : userRepository.findAllByIdIn(userIds);

        List<UserResponseDto> result =
                userMapper.toUserResponseDto(users);

        log.debug("- getUsers: {}", result);

        return result;
    }

    @Override
    public UserResponseDto createUser(UserCreateOrUpdateDto user) {
        log.debug("+ createUser: {}", user);
        UserResponseDto createdUser = userMapper.toUserResponseDto(
                userRepository.save(userMapper.toUser(user)));
        log.debug("- createUser: {}", createdUser);
        return createdUser;
    }

    @Override
    public void deleteUser(long userId) {
        log.debug("+ deleteUser: userId={}", userId);
        userRepository.delete(getUserByIdOrThrow(userId));
        log.debug("- deleteUser");
    }

    private User getUserByIdOrThrow(long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь #%d не найден.", userId)));
    }
}
