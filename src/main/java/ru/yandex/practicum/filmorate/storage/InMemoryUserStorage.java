package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> users = new HashMap<>();
    private long userId = 0;
    private static final String USER_LOG = "USER - {} : {}, user id = {}";

    @Override
    public User add(@NonNull User user) {
        ValidateService.validateUser(user);
        userId++;
        user.setUserId(userId);
        if (users.containsKey(user.getUserId())) {
            throw new AlreadyExistException(String.format(
                    "User with id = %s already exists",
                    user.getUserId()
            ));
        }
        users.put(user.getUserId(), user);
        log.info(USER_LOG, LocalDateTime.now(), "registered", user.getUserId());
        return users.get(user.getUserId());
    }

    @Override
    public User update(@NonNull long id, @NonNull User user) {
        ValidateService.validateUser(user);
        if (users.containsKey(user.getUserId())) {
            users.put(user.getUserId(), user);
            log.info(USER_LOG, LocalDateTime.now(), "updated", user.getUserId());
            return users.get(user.getUserId());
        } else {
            throw new DoNotExistException(String.format(
                    "User with id = %s do not exists",
                    user.getUserId()
            ));
        }
    }

    @Override
    public User get(@NonNull long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new DoNotExistException(String.format(
                    "User with id = %s do not exists",
                    id
            ));
        }
    }

    @Override
    public Set<User> getAll() {
        log.info("USER - {}, total amount of users: {}", LocalDateTime.now(), users.size());
        Set<User> setOfUsers = new HashSet<>();
        users.forEach((key, value) -> setOfUsers.add(value));
        return setOfUsers;
    }
}
