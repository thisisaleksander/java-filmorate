package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface UserStorage {
    User add(User user);

    User update(long id, User user);

    User get(long id);

    Set<User> getAll();
}
