package ru.practicum.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserCreateOrUpdateDto;
import ru.practicum.dto.user.UserResponseDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> userIds,
                                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("+ getUsers");
        Pageable pageable = PageRequest.of(from / size, size);
        List<UserResponseDto> users = userService.getUsers(userIds, pageable);
        log.debug("- getUsers: {}", users);
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody @Valid UserCreateOrUpdateDto user) {
        log.debug("+ createUser: {}", user);
        UserResponseDto createdUser = userService.createUser(user);
        log.debug("- createUser: {}", createdUser);
        return createdUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.debug("+ deleteUser: userId={}", userId);
        userService.deleteUser(userId);
        log.debug("- deleteUser");
    }
}
