package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    @PostMapping
    public void createUser(@RequestBody User user) {
        if(users.containsKey(user.getId())) {
            throw new AlreadyExistException(String.format(
                    "Пользователь указанным id [id = %s] уже зарегистрирован.",
                    user.getId()
            ));
        }
        users.put(user.getId(), user);
        log.info("Зарегистрирован пользователь, id = {}", user.getId());
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        users.put(user.getId(), user);
        log.info("Обновлен пользователь с id = {}", user.getId());
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }
}
