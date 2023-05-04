package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import javax.validation.Valid;
import java.util.List;
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
    public User createUser(@RequestBody @Valid User user) {
        log.info("Received POST request: new user");
        return userStorage.add(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Received PUT request: update user {}", user.getId());
        return userStorage.update(user.getId(), user);
    }

    @GetMapping
    public Set<User> findAllUsers() {
        log.info("Received GET request: all users");
        return userStorage.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        log.info("Received GET request: user {}", id);
        return userStorage.get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Received PUT request: add new friend {} to user {}", friendId, id);
        return userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends-accept/{friendId}")
    public User acceptFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Received PUT request: accept friend request from user {}", friendId);
        return userService.acceptFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Received DELETE request: delete friend {} form user {}", friendId, id);
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Received GET request: get all friends of user {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Received GET request: common friends of users {} and {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendations(@PathVariable Integer userId) {
        log.info("Received GET request: recommended films of user {}", userId);
        return userService.getRecommendations(userId);
    }
}