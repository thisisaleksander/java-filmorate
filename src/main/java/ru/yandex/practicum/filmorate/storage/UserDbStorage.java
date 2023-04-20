package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String USER_LOG = "USER - {} : {}, user id = {}";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> add(@NonNull User user) {
        ValidateService.validateUser(user);
        String sqlQuery = "insert into users (email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        log.info(USER_LOG, LocalDateTime.now(), "registered", user.getUserId());
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(@NonNull long id, @NonNull User user) {
        ValidateService.validateUser(user);
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ?" +
                " where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                id
        );
        log.info(USER_LOG, LocalDateTime.now(), "updated", user.getUserId());
        return Optional.empty();
    }

    @Override
    public Optional<User> get(@NonNull long id) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select * from employees where id = ?", id);
        if(resultSet.next()) {
            User user = mapRowToUser(resultSet);
            log.info("Found user with id = {}", user.getUserId());
            return Optional.of(user);
        } else {
            log.info("User with id = {} not found.", id);
            return Optional.empty();
        }
    }

    public User mapRowToUser(SqlRowSet resultSet) {
        return User.builder()
                .userId(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(LocalDate.ofEpochDay(resultSet.getLong("birthday")))
                .build();
    }

    @Override
    public Set<User> getAll() {
        Set<User> users = new HashSet<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select * from employees");
        if(resultSet.next()) {
            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet));
            }
            log.info("Total users found: {}", users.size());
            return users;
        } else {
            log.info("No users found");
            return Collections.emptySet();
        }
    }
}
