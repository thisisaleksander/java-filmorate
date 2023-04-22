package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserDbStorage userStorage;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<User> createUser(@RequestBody @Valid User user) {
        log.info("Received POST request");
        return userStorage.add(user);
    }

    @PutMapping
    public Optional<User> updateUser(@RequestBody @Valid User user) {
        log.info("Received PUT request");
        return userStorage.update(user.getId(), user);
    }

    @GetMapping
    public Set<User> findAllUsers() {
        log.info("Received GET request");
        return userStorage.getAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable("id") Integer id) throws SQLException {
        log.info("Received GET request");
        return userStorage.get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Optional<User> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws SQLException {
        log.info("Received PUT request");
        return userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends-accept/{friendId}")
    public Optional<User> acceptFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws SQLException {
        log.info("Received PUT request");
        return userService.acceptFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Optional<User> removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws SQLException {
        log.info("Received DELETE request");
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<Optional<User>> getFriends(@PathVariable Integer id) {
        log.info("Received GET request");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<Optional<User>>  getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Received GET request");
        return userService.getMutualFriends(id, otherId);
    }
}
