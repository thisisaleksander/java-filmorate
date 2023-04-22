package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    /**
     * @param user -> user object from json
     * @return Optional<User> -> user object that was added
     */
    Optional<User> add(User user);

    /**
     * @param id -> gets from film object
     * @param user -> user object from json
     * @return Optional<User> -> updated user object if exists
     */
    Optional<User> update(Integer id, User user);

    /**
     * @param id -> parameter from request
     * @return Optional<User> -> found user with @param id if exists
     */
    Optional<User> get(Integer id);

    /**
     *
     * @return Set<User> -> all found users in table 'users'
     */
    Set<User> getAll();
}
