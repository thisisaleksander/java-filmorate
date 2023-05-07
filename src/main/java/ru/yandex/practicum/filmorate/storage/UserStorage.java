package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.sql.SQLException;
import java.util.List;

public interface UserStorage {
    /**
     * @param user -> user object from json
     * @return User -> user object that was added
     */
    User add(User user);

    /**
     * @param id   -> gets from film object
     * @param user -> user object from json
     * @return User -> updated user object if exists
     */
    User update(Integer id, User user);

    /**
     * @param id -> parameter from request
     * @return User -> found user with @param id if exists
     */
    User get(Integer id) throws SQLException;

    /**
     * returns all user objects from table users
     *
     * @return Set<User> -> all found users in table 'users'
     */
    List<User> getAll();
}
