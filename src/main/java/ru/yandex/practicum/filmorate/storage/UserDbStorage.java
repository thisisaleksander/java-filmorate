package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    private static final String USER_LOG = "USER - {} : {}, user id = {}";

    @Override
    public Optional<User> add(@NonNull User user) {
        ValidateService.validateUser(user);
        log.info(USER_LOG, LocalDateTime.now(), "registered", user.getUserId());
        return Optional.empty();
    }

    @Override
    public Optional<User> update(@NonNull long id, @NonNull User user) {
        ValidateService.validateUser(user);
        log.info(USER_LOG, LocalDateTime.now(), "updated", user.getUserId());
        return Optional.empty();
    }

    @Override
    public Optional<User> get(@NonNull long id) {
        return Optional.empty();
    }

    @Override
    public Set<User> getAll() {
        return Collections.emptySet();
    }
}
