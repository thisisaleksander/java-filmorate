package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Map<Integer, User> users = new HashMap<>();
    private int userId = 0;
    private static final String USER_LOG = "USER - {} : {}, user id = {}";

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        ValidateService.validateUser(user);
        userId++;
        user.setId(userId);
        if (users.containsKey(user.getId())) {
            throw new AlreadyExistException(String.format(
                    "User with id = %s already exists",
                    user.getId()
            ));
        }
        users.put(user.getId(), user);
        log.info(USER_LOG, LocalDateTime.now(), "registered", user.getId());
        return users.get(user.getId());
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        ValidateService.validateUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info(USER_LOG, LocalDateTime.now(), "updated", user.getId());
            return users.get(user.getId());
        } else {
            throw new DoNotExistException(String.format(
                    "User with id = %s do not exists",
                    user.getId()
            ));
        }
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("USER - {}, total amount of users: {}", LocalDateTime.now(), users.size());
        return users.values();
    }
}
