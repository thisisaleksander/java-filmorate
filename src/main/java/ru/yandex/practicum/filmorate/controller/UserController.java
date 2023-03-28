package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    @PostMapping
    public void createUser(@RequestBody User user) {
        if(users.containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь указанным id [id = %s] уже зарегистрирован.",
                    user.getId()
            ));
        }
        users.put(user.getId(), user);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        users.put(user.getId(), user);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return users.values();
    }
}
