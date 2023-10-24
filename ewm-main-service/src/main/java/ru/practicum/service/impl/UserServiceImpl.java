package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public List<UserResponseDto> getUsers(List<Long> userIds, Pageable pageable) {
        List<User> users = (Objects.isNull(userIds) || userIds.isEmpty())
                ? userRepository.findAll(pageable).getContent()
                : userRepository.findAllByIdIn(userIds);

        return userMapper.toUserResponseDto(users);
    }

    @Override
    public UserResponseDto createUser(UserCreateOrUpdateDto user) {
        return userMapper.toUserResponseDto(
                userRepository.save(userMapper.toUser(user)));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.delete(getUserByIdOrThrow(userId));
    }

    private User getUserByIdOrThrow(long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь #%d не найден.", userId)));
    }
}
