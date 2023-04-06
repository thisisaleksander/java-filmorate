package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface UserStorage {
    User add(User user);

    User update(int id, User user);

    User get(int id);

    Set<User> getAll();
}
