package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String USER_LOG = "USER - {} : {}";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(@NonNull User user) {
        ValidateService.validateUser(user);
        String sqlQuery = "INSERT INTO users (email, login, name, birthday, deleted) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getDeleted()
        );
        log.info("PU-1. ", USER_LOG, LocalDateTime.now(), "registered");
        List<User> usersToReturn = jdbcTemplate.query("SELECT * FROM users ORDER BY id DESC LIMIT 1",
                new UserMapper());
        if (usersToReturn.isEmpty()) {
            log.info("PU-1. No users found in database");
            throw new NotFoundException("New user not found and failed to return");
        }
        return usersToReturn.get(0);
    }

    @Override
    public User update(@NonNull Integer id, @NonNull User user) {
        ValidateService.validateUser(user);
        String sqlQuery = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ?, deleted = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getDeleted(),
                id
        );
        log.info("PU-2. ", USER_LOG, LocalDateTime.now(), "updated");
        return get(id);
    }

    @Override
    public User get(@NonNull Integer id) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id);
        if (resultSet.next()) {
            User user = mapRowToUser(resultSet);
            assert user != null;
            log.info("GU-2. Found user with id = {}", user.getId());
            return user;
        } else {
            log.info("GU-2. User with id = {} not found.", id);
            throw new NotFoundException("User with id = " + id + " do not exists");
        }
    }

    public User mapRowToUser(SqlRowSet resultSet) {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(LocalDate.parse(Objects.requireNonNull(resultSet.getString("birthday"))))
                .deleted(resultSet.getBoolean("deleted"))
                .build();
    }

    @Override
    public Set<User> getAll() {
        Set<User> users = new LinkedHashSet<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        while (resultSet.next()) {
            users.add(mapRowToUser(resultSet));
        }
        log.info("GU-1. Total users found: {}", users.size());
        if (users.isEmpty()) {
            log.info("GU-1. No users found");
            return Collections.emptySet();
        }
        return users.stream()
                .sorted(User::getUserToCompare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<User> delete(Integer id) {
        String sql = "DELETE FROM USERS WHERE ID = " + id;
        jdbcTemplate.update(sql);
        log.info(String.format("DU-1. User with id = %d was deleted", id));
        return getAll();
    }
}
