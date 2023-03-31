package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.ValidateService;

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
    public void createUser(@RequestBody @Valid User user) {
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
    }

    @PutMapping
    public void updateUser(@RequestBody @Valid User user) {
        ValidateService.validateUser(user);
        users.put(user.getId(), user);
        log.info("Обновлен пользователь с id = {}", user.getId());
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }
}
