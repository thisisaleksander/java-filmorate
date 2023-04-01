package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.ValidateService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        userId++;
        user.setId(userId);
        if (users.containsKey(user.getId())) {
            throw new AlreadyExistException(String.format(
                    "Пользователь указанным id [id = %s] уже зарегистрирован.",
                    user.getId()
            ));
        }
        ValidateService.validateUser(user);
        users.put(user.getId(), user);
        log.info("Зарегистрирован пользователь, id = {}", user.getId());
        return users.get(user.getId());
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        ValidateService.validateUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь с id = {}", user.getId());
            return users.get(user.getId());
        }
        return null;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }
}
