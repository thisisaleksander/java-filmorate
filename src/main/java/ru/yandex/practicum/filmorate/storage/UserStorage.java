package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Optional<User> add(User user);

    Optional<User> update(long id, User user);

    Optional<User> get(long id);

    Set<User> getAll();
}
